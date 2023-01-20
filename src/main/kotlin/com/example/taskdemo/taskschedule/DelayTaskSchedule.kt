package com.example.taskdemo.taskschedule

import com.example.taskdemo.model.TaskContext
import java.time.Duration
import java.time.ZonedDateTime

class DelayTaskSchedule(private val fixedDelay: Duration) : TaskSchedule() {

    override fun nextExecution(taskContext: TaskContext): ZonedDateTime? {
        return taskContext.lastCompletion.plus(fixedDelay)
    }
}