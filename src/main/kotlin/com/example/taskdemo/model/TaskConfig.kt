package com.example.taskdemo.model

import com.example.taskdemo.abstractschedule.AbstractSchedule
import java.time.ZonedDateTime

class TaskConfig private constructor(

    private val schedules: List<AbstractSchedule>,

    val priority: Int,

    val heavy: Boolean,

    val enable: Boolean,

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
        var schedules: ArrayList<AbstractSchedule> = ArrayList(),
        var priority: Int = Int.MIN_VALUE,
        var heavy: Boolean = false,
        var enable: Boolean = true,
        var startDateTime: ZonedDateTime = ZonedDateTime.now()
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

        fun withStartDateTime(startDateTime: ZonedDateTime) = apply {
            this.startDateTime = startDateTime
        }

        fun withEnable(enable: Boolean) = apply {
            this.enable = enable
        }

        fun build() = TaskConfig(schedules, priority, heavy, enable, startDateTime)
    }
}
