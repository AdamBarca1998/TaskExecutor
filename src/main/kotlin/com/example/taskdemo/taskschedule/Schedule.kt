package com.example.taskdemo.taskschedule

import com.example.taskdemo.model.TaskContext
import java.time.Duration
import java.time.ZonedDateTime
import org.springframework.scheduling.support.CronExpression

abstract class Schedule {

    companion object Factory {
        @JvmStatic
        fun fromCron(cronExpression: CronExpression): Schedule {
            return CronSchedule(cronExpression)
        }

        @JvmStatic
        fun fromFixedDelay(fixedDelay: Duration): Schedule {
            return DelaySchedule(fixedDelay)
        }

        @JvmStatic
        fun fromFixedRate(fixedRate: Duration): Schedule {
            return RateSchedule(fixedRate)
        }
    }

    abstract fun nextExecution(taskContext: TaskContext): ZonedDateTime?
}
