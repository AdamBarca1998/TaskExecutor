package com.example.taskdemo.model

import com.example.taskdemo.taskschedule.TaskSchedule

abstract class TaskContextAbstract {

    abstract val taskScheduleContext: TaskScheduleContext?
    val isRollback: Boolean = false // TODO

    fun nextExecution(taskSchedule: TaskSchedule) {
    }
}
