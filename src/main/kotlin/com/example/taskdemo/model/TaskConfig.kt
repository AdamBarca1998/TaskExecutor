package com.example.taskdemo.model

import com.example.taskdemo.taskschedule.TaskSchedule
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToMany
import java.time.ZonedDateTime

@Entity
class TaskConfig private constructor(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long?,

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "task_config_id", referencedColumnName = "id", nullable = false)
    private val taskSchedules: List<TaskSchedule>,

    @Column(nullable = false)
    val priority: Int,

    @Column(nullable = false)
    val isHeavy: Boolean,

    @Column(nullable = false)
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
        var taskSchedules: List<TaskSchedule> = ArrayList(),
        var priority: Int = Int.MIN_VALUE,
        var isHeavy: Boolean = false,
        var startDateTime: ZonedDateTime = ZonedDateTime.now()
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

        fun build() = TaskConfig(null, taskSchedules, priority, isHeavy, startDateTime)
    }
}
