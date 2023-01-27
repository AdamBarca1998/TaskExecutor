package com.example.taskdemo.model

import java.time.ZonedDateTime

open class TaskContext(
    val startDateTime: ZonedDateTime,
    val lastExecution: ZonedDateTime,
    val lastCompletion: ZonedDateTime
) {
    open fun nextExecution(): ZonedDateTime? {
        throw UnsupportedOperationException("Method is not implemented for task.")
    }
}
