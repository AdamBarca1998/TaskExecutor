package com.example.taskdemo.model

import java.time.ZonedDateTime

data class TaskScheduleContext(
    val actualSchedule: ZonedDateTime,
    val lastExecution: ZonedDateTime,
    val lastCompletion: ZonedDateTime
)
