package com.example.taskdemo.model

interface Task {

    fun run(taskContext: TaskContext)

    fun getConfig(): TaskConfig
}
