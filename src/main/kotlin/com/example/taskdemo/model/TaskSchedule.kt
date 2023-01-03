package com.example.taskdemo.model

import com.cronutils.model.Cron
import com.cronutils.model.time.ExecutionTime
import com.example.taskdemo.extensions.toNullable
import java.time.ZonedDateTime

data class TaskSchedule(
    val cron: Cron
) {

    fun nextExecution(taskScheduleContext: TaskScheduleContext): ZonedDateTime? {
        return ExecutionTime.forCron(cron).nextExecution(ZonedDateTime.now()).toNullable()
    }
}
