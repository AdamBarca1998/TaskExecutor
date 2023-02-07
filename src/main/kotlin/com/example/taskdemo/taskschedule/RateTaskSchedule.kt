package com.example.taskdemo.taskschedule

import com.example.taskdemo.model.TaskContext
import jakarta.persistence.Entity
import java.time.Duration
import java.time.ZonedDateTime

class RateTaskSchedule(private val fixedRate: Duration) : TaskSchedule() {

    override fun nextExecution(taskContext: TaskContext): ZonedDateTime? {
        return taskContext.lastExecution.plus(fixedRate)
    }
}