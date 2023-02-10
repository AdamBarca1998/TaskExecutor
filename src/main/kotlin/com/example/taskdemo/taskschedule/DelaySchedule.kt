package com.example.taskdemo.taskschedule

import com.example.taskdemo.model.TaskContext
import java.time.Duration
import java.time.ZonedDateTime

class DelaySchedule(private val fixedDelay: Duration) : Schedule() {

    override fun nextExecution(taskContext: TaskContext): ZonedDateTime? {
        return taskContext.lastCompletion.plus(fixedDelay)
    }
}