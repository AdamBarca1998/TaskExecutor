package com.example.taskdemo.taskschedule

import com.cronutils.model.Cron
import com.example.taskdemo.model.TaskScheduleContext
import java.time.ZonedDateTime

class CronTaskSchedule(val cron: Cron) : TaskSchedule() {

    override fun nextExecution(taskScheduleContext: TaskScheduleContext): ZonedDateTime? {
        TODO("Not yet implemented")
    }
}