package com.example.taskdemo.model

import com.example.taskdemo.taskschedule.TaskSchedule
import java.time.ZonedDateTime

class TaskConfig private constructor(
    val taskSchedules: List<TaskSchedule>,
    val priority: Int,
    val isHeavy: Boolean,
    val description: String?
) {

    fun nextExecution(taskContext: TaskContext): ZonedDateTime? {
        val sortExecution = taskSchedules.stream()
            .map { it.nextExecution(taskContext) }
            .sorted()
            .toList()

        return sortExecution.firstOrNull()
    }

    data class Builder(
        var taskSchedules: List<TaskSchedule> = ArrayList(),
        var priority: Int = Int.MIN_VALUE,
        var isHeavy: Boolean = false,
        var startDateTime: ZonedDateTime = ZonedDateTime.now(),
        var description: String? = null
    ) {
        fun withTaskSchedules(taskSchedules: List<TaskSchedule>) = apply {
            this.taskSchedules = taskSchedules
        }

        fun withPriority(priority: Int) = apply {
            this.priority = priority
        }

        fun withHeavy(isHeavy: Boolean) = apply {
            this.isHeavy = isHeavy
        }

        fun withStartDateTime(startDateTime: ZonedDateTime) = apply {
            this.startDateTime = startDateTime
        }

        fun withDescription(description: String) = apply {
            this.description = description
        }

        fun build() = TaskConfig(taskSchedules, priority, isHeavy, description)
    }
}
