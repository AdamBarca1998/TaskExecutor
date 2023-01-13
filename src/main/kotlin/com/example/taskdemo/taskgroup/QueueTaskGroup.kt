package com.example.taskdemo.taskgroup

import java.time.ZonedDateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class QueueTaskGroup : TaskGroup() {

    override val name: String = "QueueTaskGroup"

    init {
        scope.launch(Dispatchers.IO) {
            while (!isLocked.get()) {
                plannedTasks.poll()?.let {
                    logger.debug { "${it.task} started at ${ZonedDateTime.now()}" }

                    try {
                        it.task.run(it.taskContext)
                    } catch (e: Exception) {
                        logger.error { "${it.task} e" }
                    }

                    logger.debug { "${it.task} ended at ${ZonedDateTime.now()}" }
                }

                sleepLaunch()
            }
        }
    }

    override fun start() {
        isLocked.set(false)
    }
}
