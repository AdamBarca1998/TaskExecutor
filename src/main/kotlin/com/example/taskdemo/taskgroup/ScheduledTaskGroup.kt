package com.example.taskdemo.taskgroup

import com.example.taskdemo.extensions.toNullable
import com.example.taskdemo.model.Task
import com.example.taskdemo.model.TaskScheduleContext
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.Executors
import java.util.concurrent.LinkedTransferQueue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ScheduledTaskGroup : TaskGroup() {

    override val name: String = "ScheduledTaskGroup"
    private val runningTasks = LinkedTransferQueue<TaskWithJob>()
    private val singleThreadDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()

    init {
        scope.launch(Dispatchers.IO) {
            while (true) {
                if (!isLocked.get()) {
                    while (plannedTasks.isNotEmpty()) {
                        launch {
                            plannedTasks.poll()?.let {
                                val context = if (it.taskConfig?.isHeavy == true) {
                                    singleThreadDispatcher
                                } else {
                                    Dispatchers.IO
                                }

                                val job = launch(context) { runTask(it) }

                                runningTasks.add(TaskWithJob(it, job))
                            }
                        }
                    }
                }

                sleepLaunch()
            }
        }
    }

    override fun removeTask(task: Task) {
        super.removeTask(task)
        val foundTask = runningTasks.stream().filter{it.taskWithConfigAndContext.task == task}.findFirst().toNullable()
        runningTasks.removeIf { it.taskWithConfigAndContext.task == task}
        foundTask?.job?.cancel()
    }

    private suspend fun runTask(taskWithConfigAndContext: TaskWithConfigAndContext) {
        val scheduleContext = taskWithConfigAndContext.taskContext?.taskScheduleContext

        // start
        delay(
            ChronoUnit.MILLIS.between(
                ZonedDateTime.now(),
                scheduleContext?.startDateTime
            )
        )

        do {
            scheduleContext?.lastExecution = ZonedDateTime.now()
            logger.debug { "${taskWithConfigAndContext.task} started." }

            try {
                taskWithConfigAndContext.task.run(taskWithConfigAndContext.taskContext)

                scheduleContext?.lastCompletion = ZonedDateTime.now()
                logger.debug { "${taskWithConfigAndContext.task} ended." }

                delayPeriod(taskWithConfigAndContext)
            } catch (e: Exception) {
                logger.error { "${taskWithConfigAndContext.task} $e" }

                if (taskWithConfigAndContext.taskContext?.isRollback == false) {
                    delayPeriod(taskWithConfigAndContext)
                }
            }
        } while (taskWithConfigAndContext.taskConfig?.taskSchedules?.isNotEmpty() == true && !isLocked.get())
    }

    private suspend fun delayPeriod(taskWithConfigAndContext: TaskWithConfigAndContext) {
        delay(
            ChronoUnit.MILLIS.between(
                ZonedDateTime.now(),
                taskWithConfigAndContext.taskConfig?.nextExecution(
                    taskWithConfigAndContext.taskContext?.taskScheduleContext
                        ?: TaskScheduleContext(ZonedDateTime.now(), ZonedDateTime.now(), ZonedDateTime.now())
                )
            )
        )
    }

    data class TaskWithJob(val taskWithConfigAndContext: TaskWithConfigAndContext, val job: Job)
}
