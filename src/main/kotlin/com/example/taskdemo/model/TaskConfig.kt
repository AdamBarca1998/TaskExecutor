package com.example.taskdemo.model

import com.example.taskdemo.TaskType
import com.example.taskdemo.taskschedule.TaskSchedule
import java.time.ZonedDateTime

class TaskConfig private constructor(

    private val taskSchedules: List<TaskSchedule>,

    val priority: Int,

    val isHeavy: Boolean,

    var startDateTime: ZonedDateTime,

    val type: TaskType
) {

    fun nextExecution(taskContext: TaskContext): ZonedDateTime? {
        return taskSchedules.stream()
            .map { it.nextExecution(taskContext) }
            .sorted()
            .findFirst()
            .orElse(null)
    }

    data class Builder(
        var taskSchedules: List<TaskSchedule> = ArrayList(),
        var priority: Int = Int.MIN_VALUE,
        var isHeavy: Boolean = false,
        var startDateTime: ZonedDateTime = ZonedDateTime.now(),
        var type: TaskType = TaskType.QUEUE
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

        fun withType(type: TaskType) = apply {
            this.type = type
        }

        fun build() = TaskConfig(taskSchedules, priority, isHeavy, startDateTime, type)
    }
}
