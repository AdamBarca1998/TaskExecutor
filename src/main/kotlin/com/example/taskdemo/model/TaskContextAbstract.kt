package com.example.taskdemo.model

abstract class TaskContextAbstract(val isRollback: Boolean = false) {

    abstract val taskScheduleContext: TaskScheduleContext?
}
