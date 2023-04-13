package com.example.taskdemo.model

import com.example.taskdemo.model.entities.TaskContext

interface Task {

    var id: Long

    fun run(taskContext: TaskContext)
}
