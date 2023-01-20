package com.example.taskdemo.taskgroup

import com.example.taskdemo.model.Task
import com.example.taskdemo.model.TaskConfig
import com.example.taskdemo.model.TaskContext
import com.example.taskdemo.model.TaskScheduleContext
import java.time.Duration
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

abstract class TaskGroup {

    abstract val name: String
    protected val scope = CoroutineScope(Dispatchers.Default)
    protected val plannedTasks = PriorityBlockingQueue<TaskWithConfigAndContext>()
    protected val runningTasks = LinkedTransferQueue<TaskWithJob>()
    protected var isLocked: AtomicBoolean = AtomicBoolean(false)
    protected val logger = KotlinLogging.logger {}

    fun addTask(task: Task,
                taskContext: TaskContext = TaskContext(TaskScheduleContext(ZonedDateTime.now(), ZonedDateTime.now(), ZonedDateTime.now())),
                taskConfig: TaskConfig = TaskConfig.Builder().build()) {

        plannedTasks.add(TaskWithConfigAndContext(task, taskContext, taskConfig))
    }

    open fun removeTask(task: Task) {
        plannedTasks.removeIf { it.task == task }
        runningTasks.find { it.taskWithConfigAndContext.task == task }?.let {
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
            delay(Duration.ofSeconds(10).toMillis())
        }
    }

    protected suspend fun startTask(taskWithConfigAndContext: TaskWithConfigAndContext) {
        val scheduleContext = taskWithConfigAndContext.taskContext.taskScheduleContext

        delay(ChronoUnit.MILLIS.between(ZonedDateTime.now(), scheduleContext.startDateTime))

        scheduleContext.lastExecution = ZonedDateTime.now()
        logger.debug { "${taskWithConfigAndContext.task} started." }

        try {
            taskWithConfigAndContext.task.run(taskWithConfigAndContext.taskContext)

            scheduleContext.lastCompletion = ZonedDateTime.now()
            logger.debug { "${taskWithConfigAndContext.task} ended." }
        } catch (e: Exception) {
            logger.error { "${taskWithConfigAndContext.task} $e" }
        }
    }

    protected data class TaskWithConfigAndContext(val task: Task, val taskContext: TaskContext, val taskConfig: TaskConfig): Comparable<TaskWithConfigAndContext> {

        // 1. startDateTime -> 2. priority -> 3. isHeavy
        override fun compareTo(other: TaskWithConfigAndContext): Int {
            val compareTime = taskContext.taskScheduleContext.startDateTime.compareTo(other.taskContext.taskScheduleContext.startDateTime)

            return if (compareTime == 0) {
                val comparePriority = other.taskConfig.priority.compareTo(taskConfig.priority)

                return if (comparePriority == 0) {
                    return if (taskConfig.isHeavy) {
                        -1
                    } else {
                        1
                    }
                } else {
                    comparePriority
                }
            } else {
                compareTime
            }
        }
    }

    protected data class TaskWithJob(val taskWithConfigAndContext: TaskWithConfigAndContext, val job: Job)
}
