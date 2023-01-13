package com.example.taskdemo.taskschedule

import com.example.taskdemo.model.TaskScheduleContext
import java.time.Duration
import java.time.ZonedDateTime

class RateTaskSchedule(private val fixedRate: Duration) : TaskSchedule() {

    override fun nextExecution(taskScheduleContext: TaskScheduleContext): ZonedDateTime? {
        return taskScheduleContext.lastExecution.plus(fixedRate)
    }
}