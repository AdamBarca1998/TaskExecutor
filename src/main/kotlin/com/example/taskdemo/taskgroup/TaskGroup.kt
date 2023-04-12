package com.example.taskdemo.taskgroup

import com.example.taskdemo.enums.CancelState
import com.example.taskdemo.model.Task
import com.example.taskdemo.model.TaskConfig
import com.example.taskdemo.model.TaskContext
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.CancellationException
import java.util.concurrent.LinkedTransferQueue
import java.util.concurrent.PriorityBlockingQueue
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import kotlin.random.Random
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import mu.KotlinLogging

private const val REFRESH_LOCK_TIME_M = 1
const val EXPIRED_LOCK_TIME_M = REFRESH_LOCK_TIME_M * 3
const val CLUSTER_NAME = "cluster1"

abstract class TaskGroup {

    protected val scope = CoroutineScope(Dispatchers.Default)
    protected val savedTasks: ArrayList<TaskStruct> = arrayListOf()
    protected val plannedTasks = PriorityBlockingQueue<TaskStruct>()
    protected val runningTasks = LinkedTransferQueue<TaskWithJob>()
    protected var isLocked: AtomicBoolean = AtomicBoolean(false)
    protected val logger = KotlinLogging.logger {}

    protected val port = "8081" //TODO:DELETE

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

    protected open fun handleCancel(id: Long) {
        logger.debug { "task canceled with id: $id." }
    }

    abstract fun addTask(task: Task, taskConfig: TaskConfig = TaskConfig.Builder().build())

    protected abstract suspend fun planNextExecution(taskStruct: TaskStruct, taskContext: TaskContext)

    protected open fun planNextTaskById(id: Long) {
        savedTasks.find { it.task.id == id }?.let {
            plannedTasks.add(it)
        }
    }

    fun stopGroup() {
        isLocked.set(true)
    }

    fun getAll(): List<Pair<Long, String>> {
        return savedTasks.stream()
            .map { Pair(it.task.id, it.task.javaClass.name) }
            .toList()
    }

    fun cancelTaskById(id: Long) {
        plannedTasks.removeIf { it.task.id == id }
        runningTasks.find { it.taskStruct.task.id == id }?.let {
            it.taskStruct.cancelState.set(CancelState.CANCEL)
            runningTasks.remove(it)
            it.job.cancel()
        }
        handleCancel(id)
    }

    fun runTaskById(id: Long) {
        runningTasks.find { it.taskStruct.task.id == id }?.let {
            it.taskStruct.cancelState.set(CancelState.RUN)
            it.job.cancel()
            return
        }

        plannedTasks.find { it.task.id == id }?.let {
            it.taskConfig.startDateTime = Instant.EPOCH.atZone(ZoneId.systemDefault())
            runNextTask(RunType.PARALLEL)
            return
        }

        planNextTaskById(id)
        runTaskById(id)
    }

    protected open fun runNextTask(runType: RunType = RunType.TASK_GROUP) {
        plannedTasks.poll()?.let {
            val job = scope.launch { startTask(it, runType) }

            runningTasks.add(TaskWithJob(it, job))
        }
    }

    protected suspend fun startTask(taskStruct: TaskStruct, runType: RunType = RunType.TASK_GROUP) {
        val task = taskStruct.task
        val taskContext = TaskContext(
            taskStruct.taskConfig.startDateTime,
            ZonedDateTime.now(),
            ZonedDateTime.now(),
            null
        )

        // start
        try {
            try {
                delay(ChronoUnit.MILLIS.between(ZonedDateTime.now(), taskStruct.taskConfig.startDateTime))
            } catch (e: CancellationException) {
                if (taskStruct.cancelState.get() == CancelState.CANCEL) {
                    return
                }
            }

            if (!isLocked.get() && isEnable(task)) {
                handleRun(task)

                task.run(taskContext)

                handleFinish(task)
            }
        } catch (e: Exception) {
            handleError(task, e)
        } finally {
            taskContext.lastCompletion = ZonedDateTime.now()

            // finish
            planNextExecution(taskStruct, taskContext)
            runningTasks.removeIf { it.taskStruct.task.id == taskStruct.task.id }
            if (runType != RunType.PARALLEL || runningTasks.isEmpty()) {
                runNextTask()
            }
        }
    }

    protected fun getNextRefreshMillis(): Long {
        val seconds = REFRESH_LOCK_TIME_M * 60L

        return Duration.ofSeconds(Random.nextLong(seconds / 2, seconds * 2)).toMillis()
    }

    protected data class TaskStruct(
        val task: Task,
        val taskConfig: TaskConfig,
        var cancelState: AtomicReference<CancelState> = AtomicReference(CancelState.CANCEL)
    ) : Comparable<TaskStruct> {

        // 1. startDateTime -> 2. priority
        override fun compareTo(other: TaskStruct): Int {
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

    protected data class TaskWithJob(val taskStruct: TaskStruct, val job: Job)

    protected enum class RunType {
        TASK_GROUP,
        PARALLEL
    }
}
