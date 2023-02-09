package com.example.taskdemo.model

import com.example.taskdemo.taskschedule.TaskSchedule
import java.time.ZonedDateTime

class TaskConfig private constructor(

    private val taskSchedules: List<TaskSchedule>,

    val priority: Int,

    val heavy: Boolean,

    var startDateTime: ZonedDateTime
) {

    fun nextExecution(taskContext: TaskContext): ZonedDateTime? {
        return taskSchedules.stream()
            .map { it.nextExecution(taskContext) }
            .sorted()
            .findFirst()
            .orElse(null)
    }

    data class Builder(
        var taskSchedules: ArrayList<TaskSchedule> = ArrayList(),
        var priority: Int = Int.MIN_VALUE,
        var heavy: Boolean = false,
        var startDateTime: ZonedDateTime = ZonedDateTime.now()
    ) {
        fun addTaskSchedule(taskSchedule: TaskSchedule) = apply {
            this.taskSchedules.add(taskSchedule)
        }

        fun withPriority(priority: Int) = apply {
            this.priority = priority
        }

        fun withHeavy(heavy: Boolean) = apply {
            this.heavy = heavy
        }

        fun withStartDateTime(startDateTime: ZonedDateTime) = apply {
            this.startDateTime = startDateTime
        }

        fun build() = TaskConfig(taskSchedules, priority, heavy, startDateTime)
    }
}
