package com.example.taskdemo.abstractschedule

import com.example.taskdemo.model.entities.TaskContext
import java.time.Duration
import java.time.ZonedDateTime

class RateAbstractSchedule(private val fixedRate: Duration) : AbstractSchedule() {

    override fun nextExecution(taskContext: TaskContext): ZonedDateTime? {
        return taskContext.lastExecution.plus(fixedRate)
    }
}