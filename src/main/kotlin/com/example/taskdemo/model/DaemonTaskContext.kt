package com.example.taskdemo.model

import java.time.ZonedDateTime

class DaemonTaskContext(startDateTime: ZonedDateTime, lastExecution: ZonedDateTime, lastCompletion: ZonedDateTime) :
    TaskContext(startDateTime, lastExecution, lastCompletion) {

    override fun nextExecution(): ZonedDateTime? {
        return lastCompletion.plusSeconds(30)
    }
}