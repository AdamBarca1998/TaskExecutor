package com.example.taskdemo.taskschedule

import com.example.taskdemo.model.TaskContext
import java.time.ZonedDateTime
import org.springframework.scheduling.support.CronExpression

class CronTaskSchedule(private val cronExpression: CronExpression) : TaskSchedule() {

    override fun nextExecution(taskContext: TaskContext): ZonedDateTime? {
        return cronExpression.next(ZonedDateTime.now())
    }
}