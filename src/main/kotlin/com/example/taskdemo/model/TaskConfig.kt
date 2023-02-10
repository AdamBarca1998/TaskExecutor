package com.example.taskdemo.model

import com.example.taskdemo.taskschedule.Schedule
import java.time.ZonedDateTime

class TaskConfig private constructor(

    private val schedules: List<Schedule>,

    val priority: Int,

    val heavy: Boolean,

    var startDateTime: ZonedDateTime
) {

    fun nextExecution(taskContext: TaskContext): ZonedDateTime? {
        return schedules.stream()
            .map { it.nextExecution(taskContext) }
            .sorted()
            .findFirst()
            .orElse(null)
    }

    data class Builder(
        var schedules: ArrayList<Schedule> = ArrayList(),
        var priority: Int = Int.MIN_VALUE,
        var heavy: Boolean = false,
        var startDateTime: ZonedDateTime = ZonedDateTime.now()
    ) {
        fun addSchedule(schedule: Schedule) = apply {
            this.schedules.add(schedule)
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

        fun build() = TaskConfig(schedules, priority, heavy, startDateTime)
    }
}
