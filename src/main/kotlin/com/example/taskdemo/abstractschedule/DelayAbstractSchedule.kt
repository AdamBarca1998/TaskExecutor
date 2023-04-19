package com.example.taskdemo.abstractschedule

import com.example.taskdemo.model.entities.TaskContext
import java.time.Duration
import java.time.ZonedDateTime

class DelayAbstractSchedule(private val fixedDelay: Duration) : AbstractSchedule() {

    override fun nextExecution(taskContext: TaskContext): ZonedDateTime? {
        return taskContext.lastCompletion?.plus(fixedDelay)
    }
}