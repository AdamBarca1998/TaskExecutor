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
    private val logger = KotlinLogging.logger {}

    fun addTask(task: Task, taskConfig: TaskConfig = TaskConfig.Builder().build()) {

        plannedTasks.add(TaskWithConfigAndContext(
            task,
            taskConfig,
            TaskContext(
                ZonedDateTime.now(),
                Instant.ofEpochMilli(Long.MAX_VALUE).atZone(ZoneOffset.UTC),
                Instant.ofEpochMilli(Long.MAX_VALUE).atZone(ZoneOffset.UTC)
            )
        ))
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

    protected suspend fun runTask(taskWithConfigAndContext: TaskWithConfigAndContext) {
        // begin
        delay(ChronoUnit.MILLIS.between(ZonedDateTime.now(), taskWithConfigAndContext.taskContext.startDateTime))
        val lastExecution = ZonedDateTime.now()
        logger.debug { "${taskWithConfigAndContext.task} started." }

        // run
        if (!isLocked.get()) {
            try {
                taskWithConfigAndContext.task.run(taskWithConfigAndContext.taskContext)
                logger.debug { "${taskWithConfigAndContext.task} ended." }
            } catch (e: Exception) {
                logger.error { "${taskWithConfigAndContext.task} $e" }
            }
        }

        // finish
        val lastCompletion = ZonedDateTime.now()
        runningTasks.removeIf { it.taskWithConfigAndContext == taskWithConfigAndContext }

        // next execution
        taskWithConfigAndContext.taskConfig.nextExecution(taskWithConfigAndContext.taskContext)?.let {
            val newContext = TaskContext(it, lastExecution, lastCompletion)

            plannedTasks.add(TaskWithConfigAndContext(
                taskWithConfigAndContext.task,
                taskWithConfigAndContext.taskConfig,
                newContext
            ))
        }
    }

    protected data class TaskWithConfigAndContext(val task: Task,
                                                  val taskConfig: TaskConfig,
                                                  val taskContext: TaskContext,
                                                  ) : Comparable<TaskWithConfigAndContext> {

        // 1. startDateTime -> 2. priority
        override fun compareTo(other: TaskWithConfigAndContext): Int {
            val compareTime = taskContext.startDateTime.compareTo(other.taskContext.startDateTime)

            return if (compareTime == 0) {
                val comparePriority = other.taskConfig.priority.compareTo(taskConfig.priority)

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

    protected data class TaskWithJob(val taskWithConfigAndContext: TaskWithConfigAndContext, val job: Job)
}
