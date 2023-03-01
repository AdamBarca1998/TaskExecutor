package com.example.taskdemo.model

interface Task {

    val id: Long

    fun run(taskContext: TaskContext)
}
