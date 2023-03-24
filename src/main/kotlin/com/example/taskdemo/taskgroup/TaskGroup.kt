package com.example.taskdemo.taskgroup

import com.example.taskdemo.enums.CancelState
import com.example.taskdemo.model.Task
import com.example.taskdemo.model.TaskConfig
import com.example.taskdemo.model.TaskContext
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.concurrent.CancellationException
import java.util.concurrent.LinkedTransferQueue
import java.util.concurrent.PriorityBlockingQueue
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.random.Random
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import mu.KotlinLogging

private const val REFRESH_LOCK_TIME_M = 1
const val EXPIRED_LOCK_TIME_M = REFRESH_LOCK_TIME_M * 3

abstract class TaskGroup {

    protected val scope = CoroutineScope(Dispatchers.Default)
    protected val savedTasks: ArrayList<TaskWithConfig> = arrayListOf()
    protected val plannedTasks = PriorityBlockingQueue<TaskWithConfig>()
    protected val runningTasks = LinkedTransferQueue<TaskWithJob>()
    protected var isLocked: AtomicBoolean = AtomicBoolean(false)
    protected val logger = KotlinLogging.logger {}

    protected val port = "8080" //TODO:DELETE

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

    protected open fun handleCancel(task: Task) {
        logger.debug { "$task canceled." }
    }

    abstract fun addTask(task: Task, taskConfig: TaskConfig = TaskConfig.Builder().build())

    protected abstract suspend fun planNextExecution(taskWithConfig: TaskWithConfig, taskContext: TaskContext)

    fun stopGroup() {
        isLocked.set(true)
    }

    fun getAllClazzPath(): List<String> {
        return savedTasks.stream()
            .map { it.task.javaClass.name }
            .toList()
    }

    fun cancelTaskByClazzPath(clazzPath: String) {
        savedTasks.removeIf { it.task.javaClass.name == clazzPath }
        plannedTasks.removeIf { it.task.javaClass.name == clazzPath }
        runningTasks.find { it.taskWithConfig.task.javaClass.name == clazzPath }?.let {
            it.taskWithConfig.taskConfig.cancelState.set(CancelState.CANCEL)
            runningTasks.remove(it)
            it.job.cancel()
        }
    }

    protected fun runNextTask() {
        plannedTasks.poll()?.let {
            val job = scope.launch { runTask(it) }

            runningTasks.add(TaskWithJob(it, job))
        }
    }

    protected suspend fun runTask(taskWithConfig: TaskWithConfig) {
        val task = taskWithConfig.task
        val taskContext = TaskContext(
            taskWithConfig.taskConfig.startDateTime,
            Instant.now(),
            Instant.MAX,
            null
        )

        // start
        try {
            delay(ChronoUnit.MILLIS.between(Instant.now(), taskWithConfig.taskConfig.startDateTime))
        } catch (e: CancellationException) {
            if (taskWithConfig.taskConfig.cancelState.get() == CancelState.CANCEL) {
                handleCancel(task)
                return
            }
        }

        try {
            if (!isLocked.get() && isEnable(task)) {
                handleRun(task)

                task.run(taskContext)

                handleFinish(task)
            }
        } catch (e: Exception) {
            handleError(task, e)
        } finally {
            taskContext.lastCompletion = Instant.now()

            // finish
            planNextExecution(taskWithConfig, taskContext)
            runningTasks.removeIf { it.taskWithConfig == taskWithConfig }
            runNextTask()
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
