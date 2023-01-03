package com.example.taskdemo.model

abstract class TaskContextAbstract {

    abstract val taskScheduleContext: TaskScheduleContext?;
    val isRollback: Boolean = false;

    fun nextExecution(taskSchedule: TaskSchedule) {
    }
}
