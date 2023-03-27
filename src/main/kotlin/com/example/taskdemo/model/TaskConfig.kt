package com.example.taskdemo.model

import com.example.taskdemo.abstractschedule.AbstractSchedule
import com.example.taskdemo.enums.CancelState
import java.time.ZonedDateTime
import java.util.concurrent.atomic.AtomicReference

class TaskConfig private constructor(

    private val schedules: List<AbstractSchedule>,

    val priority: Int,

    val heavy: Boolean,

    var startDateTime: ZonedDateTime,

    var cancelState: AtomicReference<CancelState>
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
        var startDateTime: ZonedDateTime = ZonedDateTime.now(),
        var cancelState: AtomicReference<CancelState> = AtomicReference(CancelState.CANCEL)
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

        fun withCancelState(cancelState: CancelState) = apply {
            this.cancelState.set(cancelState)
        }

        fun build() = TaskConfig(schedules, priority, heavy, startDateTime, cancelState)
    }
}
