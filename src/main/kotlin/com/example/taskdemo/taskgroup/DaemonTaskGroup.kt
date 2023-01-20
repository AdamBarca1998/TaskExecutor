package com.example.taskdemo.taskgroup

import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DaemonTaskGroup : TaskGroup() {

    override val name: String = "DaemonTaskGroup"

    init {
        scope.launch(Dispatchers.IO) {
            while (true) {
                if (!isLocked.get()) {
                    while (plannedTasks.isNotEmpty()) {
                        launch {
                            plannedTasks.poll()?.let {
                                val job = launch { runTask(it) }

                                runningTasks.add(TaskWithJob(it, job))
                            }
                        }
                    }
                }

                sleepLaunch()
            }
        }
    }

    private suspend fun runTask(taskWithConfigAndContext: TaskWithConfigAndContext) {
        while (true) {
            if (!isLocked.get()) {
                startTask(taskWithConfigAndContext)
            }

            taskWithConfigAndContext.taskConfig.nextExecution(taskWithConfigAndContext.taskContext.taskScheduleContext)
                .let {
                    val datetime = it ?: ZonedDateTime.now().plusHours(1)

                    taskWithConfigAndContext.taskContext.taskScheduleContext.startDateTime = datetime
                    delay(ChronoUnit.MILLIS.between(ZonedDateTime.now(), datetime))
                }
        }
    }
}