package com.example.taskdemo.dto

import com.example.taskdemo.enums.TaskState
import java.time.ZonedDateTime

data class QueueTaskDto(
    val id: Long,
    val clazz: String,
    val state: TaskState,
    val createdAt: ZonedDateTime
)
