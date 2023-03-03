package com.example.taskdemo.taskgroup

import com.example.taskdemo.model.Task
import com.example.taskdemo.model.TaskConfig
import com.example.taskdemo.model.TaskContext
import java.time.Duration
import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.LinkedTransferQueue
import java.util.concurrent.PriorityBlockingQueue
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.random.Random
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import mu.KotlinLogging

private const val REFRESH_LOCK_TIME_M = 1
const val EXPIRED_LOCK_TIME_M = REFRESH_LOCK_TIME_M * 3
private const val LAUNCH_DELAY_TIME_S = 1L

abstract class TaskGroup {

    protected val scope = CoroutineScope(Dispatchers.Default)
    protected val plannedTasks = PriorityBlockingQueue<TaskWithConfig>()
    protected val runningTasks = LinkedTransferQueue<TaskWithJob>()
    protected var isLocked: AtomicBoolean = AtomicBoolean(false)
    protected val logger = KotlinLogging.logger {}

    protected abstract fun isEnable(task: Task): Boolean

    protected open fun handleRun(task: Task) {
        logger.debug { "$task started." }
    }

    protected open fun handleError(task: Task, e: Exception) {
        logger.error { "$task $e" }
    }

    protected open fun handleFinish(task: Task) {
        logger.debug { "$task ended." }
    }

    open fun addTask(task: Task, taskConfig: TaskConfig = TaskConfig.Builder().build()) {
        plannedTasks.add(TaskWithConfig(task, taskConfig))
    }

    open fun removeTask(task: Task) {
        plannedTasks.removeIf { it.task == task }
        runningTasks.find { it.taskWithConfig.task == task }?.let {
            runningTasks.remove(it)
            it.job.cancel()
        }
    }

    fun start() {
        isLocked.set(false)
    }

    fun stop() {
        isLocked.set(true)
    }

    protected suspend fun sleepLaunch() {
        if (plannedTasks.isEmpty() || isLocked.get()) {
            delay(Duration.ofSeconds(LAUNCH_DELAY_TIME_S).toMillis())
        }
    }

    protected suspend fun runTask(taskWithConfig: TaskWithConfig) {
        var lastExecution: ZonedDateTime? = null
        val config = taskWithConfig.taskConfig
        val task = taskWithConfig.task

        // start
        delay(ChronoUnit.MILLIS.between(ZonedDateTime.now(), config.startDateTime))

        if (!isLocked.get() && isEnable(task)) {
            handleRun(task)
            lastExecution = ZonedDateTime.now()

            try {
                task.run(
                    TaskContext(
                        config.startDateTime,
                        lastExecution, Instant.ofEpochMilli(Long.MAX_VALUE).atZone(ZoneOffset.UTC)
                    )
                )

                handleFinish(task)
            } catch (e: Exception) {
                handleError(task, e)
            }
        }

        // finish
        planNextExecution(taskWithConfig, lastExecution ?: ZonedDateTime.now(), ZonedDateTime.now())
        runningTasks.removeIf { it.taskWithConfig == taskWithConfig }
    }

    protected open fun planNextExecution(taskWithConfig: TaskWithConfig,
                                         lastExecution: ZonedDateTime,
                                         lastCompletion: ZonedDateTime
    ) {
        taskWithConfig.taskConfig.nextExecution(
            TaskContext(taskWithConfig.taskConfig.startDateTime, lastExecution, lastCompletion)
        )?.let {
            taskWithConfig.taskConfig.startDateTime = it
            plannedTasks.add(TaskWithConfig(taskWithConfig.task, taskWithConfig.taskConfig))
        }
    }

    protected fun getNextRefreshMillis(): Long {
        val seconds = REFRESH_LOCK_TIME_M * 60L

        return Duration.ofSeconds(Random.nextLong(seconds / 2, seconds * 2)).toMillis()
    }

    protected data class TaskWithConfig(val task: Task, val taskConfig: TaskConfig) : Comparable<TaskWithConfig> {

        // 1. startDateTime -> 2. priority
        override fun compareTo(other: TaskWithConfig): Int {
            val compareTime = taskConfig.startDateTime.compareTo(other.taskConfig.startDateTime)

            return if (compareTime == 0) {
                val comparePriority = other.taskConfig.priority.compareTo(taskConfig.priority)

                // due to original order
                if (comparePriority == 0) {
                    1
                } else {
                    comparePriority
                }
            } else {
                compareTime
            }
        }
    }

    protected data class TaskWithJob(val taskWithConfig: TaskWithConfig, val job: Job)
}
