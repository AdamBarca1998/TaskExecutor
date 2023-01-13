package com.example.taskdemo.taskschedule

import com.example.taskdemo.model.TaskScheduleContext
import java.time.Duration
import java.time.ZonedDateTime

class DelayTaskSchedule(private val fixedDelay: Duration) : TaskSchedule() {

    override fun nextExecution(taskScheduleContext: TaskScheduleContext): ZonedDateTime? {
        return taskScheduleContext.lastCompletion.plus(fixedDelay)
    }
}