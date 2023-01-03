package com.example.taskdemo.taskgroup

import com.example.taskdemo.model.TaskScheduleContext
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.LinkedTransferQueue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ScheduledTaskGroup : TaskGroupAbstract() {

    override val name: String = "ScheduledTaskGroup"
    private val runningTasks = LinkedTransferQueue<TaskWithConfigAndContext>()

    init {
        start()
    }

    override fun start() {
        if (isLocked) {
            isLocked = false

            runningTasks.forEach {
                scope.launch {
                    runTask(it)
                }
            }
        }

        scope.launch(Dispatchers.IO) {
            while (!isLocked) {
                while (plannedTasks.isNotEmpty()) {
                    launch {
                        plannedTasks.poll()?.let {
                            runningTasks.add(it)
                            runTask(it)
                        }
                    }
                }

                sleepLaunch()
            }
        }
    }

    private suspend fun runTask(taskWithConfigAndContext: TaskWithConfigAndContext) {
        // start
        delay(
            ChronoUnit.MILLIS.between(
                ZonedDateTime.now(),
                taskWithConfigAndContext.taskContext?.taskScheduleContext?.startDateTime ?: ZonedDateTime.now()
            )
        )

        do {
            taskWithConfigAndContext.task.run(taskWithConfigAndContext.taskContext)
            // period
            delay(
                ChronoUnit.MILLIS.between(
                    ZonedDateTime.now(),
                    taskWithConfigAndContext.taskConfig?.nextExecution(
                        TaskScheduleContext(
                            ZonedDateTime.now(),
                            ZonedDateTime.now(),
                            ZonedDateTime.now()
                        )
                    ) ?: ZonedDateTime.now()
                )
            )
        } while (taskWithConfigAndContext.taskConfig?.taskSchedules?.isNotEmpty() == true && !isLocked)
    }
}
