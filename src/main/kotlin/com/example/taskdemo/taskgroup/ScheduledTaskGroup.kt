package com.example.taskdemo.taskgroup

import com.example.taskdemo.model.TaskContext
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ScheduledTaskGroup : TaskGroupAbstract() {

    init {
        scope.launch(Dispatchers.IO) {
            while (true) {
                while (plannedTasks.isNotEmpty()) {
                    launch {
                        plannedTasks.poll()?.let {
                            delay(ChronoUnit.MILLIS.between(ZonedDateTime.now(), it.startTime)) // start

                            do {
                                it.run(TaskContext())
                                delay(
                                    ChronoUnit.MILLIS.between(
                                        ZonedDateTime.now(),
                                        it.getNextTime() ?: ZonedDateTime.now())
                                ) // period
                            } while (it.cronList != null)
                        }
                    }
                }

                sleepLaunch()
            }
        }
    }
}
