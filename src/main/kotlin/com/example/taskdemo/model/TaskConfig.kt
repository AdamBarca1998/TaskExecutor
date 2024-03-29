package com.example.taskdemo.model

import com.example.taskdemo.abstractschedule.AbstractSchedule
import com.example.taskdemo.enums.ScheduleTaskType
import com.example.taskdemo.model.entities.TaskContext
import java.time.Duration
import java.time.ZonedDateTime

class TaskConfig private constructor(

    private val schedules: List<AbstractSchedule>,

    val priority: Int,

    val type: ScheduleTaskType,

    val startDateTime: ZonedDateTime,

    val maxWaitDuration: Duration
) {

    fun nextExecution(taskContext: TaskContext): ZonedDateTime? {
        return schedules.stream()
            .map { it.nextExecution(taskContext) }
            .sorted()
            .findFirst()
            .orElse(null)
    }

    data class Builder(
        var schedules: ArrayList<AbstractSchedule> = ArrayList(),
        var priority: Int = Int.MIN_VALUE,
        var type: ScheduleTaskType = ScheduleTaskType.NORMAL,
        var startDateTime: ZonedDateTime = ZonedDateTime.now(),
        var maxWaitDuration: Duration = Duration.ofDays(1)
    ) {
        fun addSchedule(schedules: AbstractSchedule) = apply {
            this.schedules.add(schedules)
        }

        fun withPriority(priority: Int) = apply {
            this.priority = priority
        }

        fun withType(type: ScheduleTaskType) = apply {
            this.type = type
        }

        fun withStartDateTime(startDateTime: ZonedDateTime) = apply {
            this.startDateTime = startDateTime
        }

        fun withMaxWaitDuration(maxWaitDuration: Duration) = apply {
            this.maxWaitDuration = maxWaitDuration
        }

        fun build() = TaskConfig(schedules, priority, type, startDateTime, maxWaitDuration)
    }
}
