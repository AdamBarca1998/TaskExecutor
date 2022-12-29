package com.example.taskdemo.taskgroup

import com.example.taskdemo.model.TaskContext
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BasicTaskGroup : TaskGroupAbstract() {

    fun scheduleRun() {

    }

    override fun run() {
        scope.launch {
            val task = withContext(Dispatchers.IO) {
                plannedTasks.take()
            }

            delay(ChronoUnit.MILLIS.between(ZonedDateTime.now(), task.startTime)) // start

            do {
                task.run(TaskContext())
                delay(ChronoUnit.MILLIS.between(ZonedDateTime.now(), task.getNextTime() ?: ZonedDateTime.now())) // period
            } while (task.cronList != null)
        }
    }
}
