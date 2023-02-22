package com.example.taskdemo.taskgroup

import com.example.taskdemo.model.Task
import com.example.taskdemo.model.TaskConfig
import com.example.taskdemo.model.TaskContext
import com.example.taskdemo.service.ScheduleTaskService
import java.time.Duration
import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.LinkedTransferQueue
import java.util.concurrent.PriorityBlockingQueue
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import mu.KotlinLogging

const val REFRESH_LOCK_TIME_M = 1
const val EXPIRED_LOCK_TIME_M = REFRESH_LOCK_TIME_M * 3
private const val LAUNCH_DELAY_TIME_S = 1L

abstract class TaskGroup(
    private val scheduleTaskService: ScheduleTaskService
) {

    abstract val name: String
    protected val scope = CoroutineScope(Dispatchers.Default)
    protected val plannedTasks = PriorityBlockingQueue<TaskWithConfig>()
    protected val runningTasks = LinkedTransferQueue<TaskWithJob>()
    protected var isLocked: AtomicBoolean = AtomicBoolean(false)
    private val logger = KotlinLogging.logger {}

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

        // start
        delay(ChronoUnit.MILLIS.between(ZonedDateTime.now(), config.startDateTime))

        if (scheduleTaskService.isEnableByClazzPath(taskWithConfig.task.javaClass.name) && !isLocked.get()) {
            lastExecution = ZonedDateTime.now()

            try {
                // run
                logger.debug { "${taskWithConfig.task} started." }

                taskWithConfig.task.run(
                    TaskContext(
                        config.startDateTime,
                        lastExecution, Instant.ofEpochMilli(Long.MAX_VALUE).atZone(ZoneOffset.UTC)
                    )
                )

                logger.debug { "${taskWithConfig.task} ended." }
            } catch (e: Exception) {
                logger.error { "${taskWithConfig.task} $e" }
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
