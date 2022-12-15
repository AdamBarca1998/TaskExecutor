package com.example.taskdemo.interfaces

import com.example.taskdemo.model.Task

interface TaskGroup {

    fun addAllAndRun(tasks: List<Task>)
}