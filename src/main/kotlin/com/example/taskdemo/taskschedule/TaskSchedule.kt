package com.example.taskdemo.taskschedule

import com.example.taskdemo.model.TaskContext
import java.time.Duration
import java.time.ZonedDateTime
import org.springframework.scheduling.support.CronExpression

abstract class TaskSchedule {

    companion object Factory {
        @JvmStatic
        fun fromCron(cronExpression: CronExpression): TaskSchedule {
            return CronTaskSchedule(cronExpression)
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
