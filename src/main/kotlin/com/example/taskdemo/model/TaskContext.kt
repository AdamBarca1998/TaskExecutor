package com.example.taskdemo.model

import java.time.ZonedDateTime

data class TaskContext(
    val startDateTime: ZonedDateTime,
    val lastExecution: ZonedDateTime,
    val lastCompletion: ZonedDateTime
)
