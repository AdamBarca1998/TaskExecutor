package com.example.taskdemo.model

class TaskConfig private constructor(
    val taskSchedules: List<TaskSchedule>,
    val priority: Int,
    val isHeavy: Boolean,
    val description: String?
) {

    data class Builder(
        var taskSchedules: List<TaskSchedule> = ArrayList(),
        var priority: Int = Int.MIN_VALUE,
        var isHeavy: Boolean = false,
        var description: String? = null
    ) {
        fun taskSchedules(taskSchedules: List<TaskSchedule>) = apply {
            this.taskSchedules = taskSchedules
        }

        fun priority(priority: Int) = apply {
            this.priority = priority
        }

        fun isHeavy(isHeavy: Boolean) = apply {
            this.isHeavy = isHeavy
        }

        fun description(description: String) = apply {
            this.description = description
        }

        fun build() = TaskConfig(taskSchedules, priority, isHeavy, description)
    }
}
