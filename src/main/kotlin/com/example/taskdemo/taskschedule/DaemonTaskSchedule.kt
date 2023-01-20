package com.example.taskdemo.taskschedule

import com.example.taskdemo.model.TaskContext
import java.time.ZonedDateTime

class DaemonTaskSchedule : TaskSchedule() {

    private val events = listOf(
        ZonedDateTime.now().plusSeconds(10),
        ZonedDateTime.now().plusSeconds(30),
        ZonedDateTime.now().plusMinutes(60)
    )

    override fun nextExecution(taskContext: TaskContext): ZonedDateTime? {
        return events.find { ZonedDateTime.now().isBefore(it) }
    }
}