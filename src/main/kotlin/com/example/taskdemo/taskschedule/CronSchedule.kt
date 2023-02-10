package com.example.taskdemo.taskschedule

import com.example.taskdemo.model.TaskContext
import java.time.ZonedDateTime
import org.springframework.scheduling.support.CronExpression

class CronSchedule(private val cronExpression: CronExpression) : Schedule() {

    override fun nextExecution(taskContext: TaskContext): ZonedDateTime? {
        return cronExpression.next(ZonedDateTime.now())
    }
}