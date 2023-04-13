package com.example.taskdemo.abstractschedule

import com.example.taskdemo.model.entities.TaskContext
import java.time.ZonedDateTime
import org.springframework.scheduling.support.CronExpression

class CronAbstractSchedule(private val cronExpression: CronExpression) : AbstractSchedule() {

    override fun nextExecution(taskContext: TaskContext): ZonedDateTime? {
        return cronExpression.next(ZonedDateTime.now())
    }
}