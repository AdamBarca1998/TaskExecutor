package com.example.taskdemo.model

import java.time.ZonedDateTime

data class TaskScheduleContext(
    var startDateTime: ZonedDateTime,
    var lastExecution: ZonedDateTime,
    var lastCompletion: ZonedDateTime
)
