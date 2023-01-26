package com.example.taskdemo.taskschedule

import com.cronutils.model.Cron
import com.example.taskdemo.model.TaskContext
import java.time.Duration
import java.time.ZonedDateTime

abstract class TaskSchedule {

    companion object Factory {
        @JvmStatic
        fun fromCron(cron: Cron): TaskSchedule {
            return CronTaskSchedule(cron)
        }

        @JvmStatic
        fun fromFixedDelay(fixedDelay: Duration): TaskSchedule {
            return DelayTaskSchedule(fixedDelay)
        }

        @JvmStatic
        fun fromFixedRate(fixedRate: Duration): TaskSchedule {
            return RateTaskSchedule(fixedRate)
        }
    }

    abstract fun nextExecution(taskContext: TaskContext): ZonedDateTime?
}
