package com.example.taskdemo.model

import com.example.taskdemo.abstractschedule.AbstractSchedule
import java.time.Instant

class TaskConfig private constructor(

    private val schedules: List<AbstractSchedule>,

    val priority: Int,

    val heavy: Boolean,

    var startDateTime: Instant
) {

    fun nextExecution(taskContext: TaskContext): Instant? {
        return schedules.stream()
            .map { it.nextExecution(taskContext) }
            .sorted()
            .findFirst()
            .orElse(null)
    }

    data class Builder(
        var schedules: ArrayList<AbstractSchedule> = ArrayList(),
        var priority: Int = Int.MIN_VALUE,
        var heavy: Boolean = false,
        var startDateTime: Instant = Instant.now()
    ) {
        fun addSchedule(schedules: AbstractSchedule) = apply {
            this.schedules.add(schedules)
        }

        fun withPriority(priority: Int) = apply {
            this.priority = priority
        }

        fun withHeavy(heavy: Boolean) = apply {
            this.heavy = heavy
        }

        fun withStartDateTime(startDateTime: Instant) = apply {
            this.startDateTime = startDateTime
        }

        fun build() = TaskConfig(schedules, priority, heavy, startDateTime)
    }
}
