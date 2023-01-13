package com.example.taskdemo.taskgroup

import java.time.ZonedDateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class QueueTaskGroup : TaskGroup() {

    override val name: String = "QueueTaskGroup"

    init {
        start()
    }

    override fun start() {
        isLocked = false

        scope.launch(Dispatchers.IO) {
            while (!isLocked) {
                plannedTasks.poll()?.let {
                    logger.debug { "${it.task} started at ${ZonedDateTime.now()}" }
                    it.task.run(it.taskContext)
                    logger.debug { "${it.task} ended at ${ZonedDateTime.now()}" }
                }

                sleepLaunch()
            }
        }
    }
}
