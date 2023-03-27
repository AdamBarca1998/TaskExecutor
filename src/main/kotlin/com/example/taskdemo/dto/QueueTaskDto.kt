package com.example.taskdemo.dto

import com.example.taskdemo.enums.QueueTaskState
import java.time.ZonedDateTime

data class QueueTaskDto(
    val id: Long,
    val clazz: String,
    val state: QueueTaskState,
    val createdAt: ZonedDateTime
)
