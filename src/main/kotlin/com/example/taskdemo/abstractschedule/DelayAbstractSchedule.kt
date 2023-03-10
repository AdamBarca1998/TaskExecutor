package com.example.taskdemo.abstractschedule

import com.example.taskdemo.model.TaskContext
import java.time.Duration
import java.time.Instant

class DelayAbstractSchedule(private val fixedDelay: Duration) : AbstractSchedule() {

    override fun nextExecution(taskContext: TaskContext): Instant? {
        return taskContext.lastCompletion.plus(fixedDelay)
    }
}