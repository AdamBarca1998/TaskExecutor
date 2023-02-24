package com.example.taskdemo.taskgroup

import com.example.taskdemo.model.Task
import com.example.taskdemo.model.TaskConfig
import com.example.taskdemo.service.QueueTaskService

class QueueTaskGroup(
    private val queueTaskService: QueueTaskService
) : SerializedTaskGroup() {

    override fun isEnable(task: Task): Boolean = true

    override fun addTask(task: Task, taskConfig: TaskConfig) {
        queueTaskService.createTask(task)
    }
}