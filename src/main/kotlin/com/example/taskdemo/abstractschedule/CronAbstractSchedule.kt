package com.example.taskdemo.abstractschedule

import com.example.taskdemo.model.TaskContext
import java.time.Instant
import java.time.ZonedDateTime
import org.springframework.scheduling.support.CronExpression

class CronAbstractSchedule(private val cronExpression: CronExpression) : AbstractSchedule() {

    override fun nextExecution(taskContext: TaskContext): Instant? {
        return cronExpression.next(ZonedDateTime.now())?.toInstant()
    }
}