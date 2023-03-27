package com.example.taskdemo.model

import java.time.ZonedDateTime

class TaskContext(
    val startDateTime: ZonedDateTime,
    val lastExecution: ZonedDateTime,
    var lastCompletion: ZonedDateTime,
    var nextExecution: ZonedDateTime?
)
