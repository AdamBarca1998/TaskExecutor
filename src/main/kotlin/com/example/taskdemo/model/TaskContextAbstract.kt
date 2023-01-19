package com.example.taskdemo.model

abstract class TaskContextAbstract(open val isRollback: Boolean = false) {

    abstract val taskScheduleContext: TaskScheduleContext?
}
