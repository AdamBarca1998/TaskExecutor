package com.example.taskdemo.taskschedule

import com.cronutils.model.Cron
import com.cronutils.model.time.ExecutionTime
import com.example.taskdemo.extensions.toNullable
import com.example.taskdemo.model.TaskContext
import java.time.ZonedDateTime

class CronTaskSchedule(private val cron: Cron) : TaskSchedule() {

    override fun nextExecution(taskContext: TaskContext): ZonedDateTime? {
        return ExecutionTime.forCron(cron).nextExecution(ZonedDateTime.now()).toNullable()
    }
}