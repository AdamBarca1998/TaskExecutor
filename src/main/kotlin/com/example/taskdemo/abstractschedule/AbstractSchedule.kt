package com.example.taskdemo.abstractschedule

import com.example.taskdemo.model.TaskContext
import java.time.Duration
import java.time.Instant
import org.springframework.scheduling.support.CronExpression

abstract class AbstractSchedule {

    companion object Factory {
        @JvmStatic
        fun fromCron(cronExpression: CronExpression): AbstractSchedule {
            return CronAbstractSchedule(cronExpression)
        }

        @JvmStatic
        fun fromFixedDelay(fixedDelay: Duration): AbstractSchedule {
            return DelayAbstractSchedule(fixedDelay)
        }

        @JvmStatic
        fun fromFixedRate(fixedRate: Duration): AbstractSchedule {
            return RateAbstractSchedule(fixedRate)
        }
    }

    abstract fun nextExecution(taskContext: TaskContext): Instant?
}
