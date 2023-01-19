package com.example.taskdemo.taskgroup

import com.example.taskdemo.extensions.toNullable
import com.example.taskdemo.model.Task
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.Executors
import java.util.concurrent.LinkedTransferQueue
import java.util.concurrent.PriorityBlockingQueue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ScheduledTaskGroup : TaskGroup() {

    override val name: String = "ScheduledTaskGroup"
    private val runningTasks = LinkedTransferQueue<TaskWithJob>()
    private val runningHeavyTasks = LinkedTransferQueue<TaskWithJob>()
    private val sortedTask = PriorityBlockingQueue<TaskWithConfigAndContext>()
    private val singleThreadDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()

    init {
        scope.launch(Dispatchers.IO) {
            while (true) {
                if (!isLocked.get()) {
                    // moveAll tasks from planned to sorted
                    while (plannedTasks.isNotEmpty()) {
                        sortedTask.add(plannedTasks.poll())
                    }

                    if (runningTasks.isEmpty() || sortedTask.first().taskConfig.isHeavy) {
                        runTask()
                    }
                }

                sleepLaunch()
            }
        }
    }

    override fun removeTask(task: Task) {
        super.removeTask(task)
        runningTasks.stream().filter { it.taskWithConfigAndContext.task == task }.findFirst().toNullable()?.let {
            runningTasks.remove(it)
            it.job.cancel()
        }
        runningHeavyTasks.stream().filter { it.taskWithConfigAndContext.task == task }.findFirst().toNullable()?.let {
            runningHeavyTasks.remove(it)
            it.job.cancel()
        }
    }

    private suspend fun runTask() {
        val taskWithConfigAndContext = sortedTask.poll()

        if (taskWithConfigAndContext != null) {
            val context = if (taskWithConfigAndContext.taskConfig.isHeavy) {
                singleThreadDispatcher
            } else {
                Dispatchers.IO
            }

            val job = scope.launch(context) {
                val scheduleContext = taskWithConfigAndContext.taskContext.taskScheduleContext

                // start
                delay(
                    ChronoUnit.MILLIS.between(
                        ZonedDateTime.now(),
                        scheduleContext.startDateTime
                    )
                )

                scheduleContext.lastExecution = ZonedDateTime.now()
                logger.debug { "${taskWithConfigAndContext.task} started." }

                try {
                    taskWithConfigAndContext.task.run(taskWithConfigAndContext.taskContext)

                    scheduleContext.lastCompletion = ZonedDateTime.now()
                    logger.debug { "${taskWithConfigAndContext.task} ended." }
                } catch (e: Exception) {
                    logger.error { "${taskWithConfigAndContext.task} $e" }
                }

                taskWithConfigAndContext.taskConfig.nextExecution(taskWithConfigAndContext.taskContext.taskScheduleContext)?.let {
                    taskWithConfigAndContext.taskContext.taskScheduleContext.startDateTime = it
                    sortedTask.add(taskWithConfigAndContext)
                }

                runningTasks.removeIf { it.taskWithConfigAndContext == taskWithConfigAndContext }
                runningHeavyTasks.removeIf { it.taskWithConfigAndContext == taskWithConfigAndContext }

                if (!taskWithConfigAndContext.taskConfig.isHeavy) {
                    runTask()
                }
            }

            if (taskWithConfigAndContext.taskConfig.isHeavy) {
                runningHeavyTasks.add(TaskWithJob(taskWithConfigAndContext, job))
            } else {
                runningTasks.add(TaskWithJob(taskWithConfigAndContext, job))
            }
        }
    }

    data class TaskWithJob(val taskWithConfigAndContext: TaskWithConfigAndContext, val job: Job)
}
