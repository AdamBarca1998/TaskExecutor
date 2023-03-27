package com.example.taskdemo.model

interface Task {

    var id: Long

    fun run(taskContext: TaskContext)
}
