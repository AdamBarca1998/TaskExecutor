package com.example.taskdemo.taskschedule

import com.example.taskdemo.model.TaskScheduleContext
import java.time.Duration
import java.time.ZonedDateTime

class DelayTaskSchedule(val fixedDelay: Duration) : TaskSchedule() {

    override fun nextExecution(taskScheduleContext: TaskScheduleContext): ZonedDateTime? {
        TODO("Not yet implemented")
    }
}