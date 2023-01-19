package com.example.taskdemo.model

class TaskContext(
    override val taskScheduleContext: TaskScheduleContext,
    override val isRollback: Boolean = false) : TaskContextAbstract() {
}