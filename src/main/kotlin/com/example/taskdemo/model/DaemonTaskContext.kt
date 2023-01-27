package com.example.taskdemo.model

import java.time.ZonedDateTime

class DaemonTaskContext(startDateTime: ZonedDateTime, lastExecution: ZonedDateTime, lastCompletion: ZonedDateTime) :
    TaskContext(startDateTime, lastExecution, lastCompletion) {

    private val events = listOf(
        ZonedDateTime.now().plusSeconds(10),
        ZonedDateTime.now().plusSeconds(30),
        ZonedDateTime.now().plusSeconds(60)
    )

    override fun nextExecution(): ZonedDateTime? {
        return events.find { ZonedDateTime.now().isBefore(it) }
    }
}