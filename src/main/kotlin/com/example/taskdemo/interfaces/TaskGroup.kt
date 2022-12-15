package com.example.taskdemo.interfaces

import com.example.taskdemo.model.TaskImpl

interface TaskGroup {

    fun addAllAndRun(tasks: List<TaskImpl>)
}