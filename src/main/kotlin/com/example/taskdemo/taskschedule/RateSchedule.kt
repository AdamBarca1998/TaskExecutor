package com.example.taskdemo.taskschedule

import com.example.taskdemo.model.TaskContext
import java.time.Duration
import java.time.ZonedDateTime

class RateSchedule(private val fixedRate: Duration) : Schedule() {

    override fun nextExecution(taskContext: TaskContext): ZonedDateTime? {
        return taskContext.lastExecution.plus(fixedRate)
    }
}