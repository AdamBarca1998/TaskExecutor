package com.example.taskdemo.taskgroup

import com.example.taskdemo.extensions.toNullable
import com.example.taskdemo.model.Task
import com.example.taskdemo.model.TaskScheduleContext
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.LinkedTransferQueue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ScheduledTaskGroup : TaskGroup() {

    override val name: String = "ScheduledTaskGroup"
    private val runningTasks = LinkedTransferQueue<TaskWithJob>()

    init {
        start()
    }

    override fun start() {
        isLocked = false

        scope.launch(Dispatchers.IO) {
            while (!isLocked) {
                while (plannedTasks.isNotEmpty()) {
                    launch {
                        plannedTasks.poll()?.let {
                            val job = launch { runTask(it) }

                            runningTasks.add(TaskWithJob(it, job))
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
            logger.debug { "${taskWithConfigAndContext.task} started at ${scheduleContext?.lastExecution} actualTime: ${ZonedDateTime.now()}" }

            taskWithConfigAndContext.task.run(taskWithConfigAndContext.taskContext)

            scheduleContext?.lastCompletion = ZonedDateTime.now()
            logger.debug { "${taskWithConfigAndContext.task} ended at ${scheduleContext?.lastCompletion} actualTime: ${ZonedDateTime.now()}" }

            // period
            delay(
                ChronoUnit.MILLIS.between(
                    ZonedDateTime.now(),
                    taskWithConfigAndContext.taskConfig?.nextExecution(
                        scheduleContext ?: TaskScheduleContext(ZonedDateTime.now(), ZonedDateTime.now(), ZonedDateTime.now())
                    )
                )
            )
        } while (taskWithConfigAndContext.taskConfig?.taskSchedules?.isNotEmpty() == true && !isLocked)
    }

    data class TaskWithJob(val taskWithConfigAndContext: TaskWithConfigAndContext, val job: Job)
}
