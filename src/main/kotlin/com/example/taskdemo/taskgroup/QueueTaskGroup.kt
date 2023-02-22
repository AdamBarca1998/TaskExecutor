package com.example.taskdemo.taskgroup

import com.example.taskdemo.service.TaskService

class QueueTaskGroup(
    private val taskService: TaskService,
) : SerializedTaskGroup(
    taskService
) {

}