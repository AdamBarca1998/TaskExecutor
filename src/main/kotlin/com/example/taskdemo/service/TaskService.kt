package com.example.taskdemo.service

import com.example.taskdemo.model.Task
import com.example.taskdemo.model.TaskConfig
import com.example.taskdemo.model.TaskContext
import com.example.taskdemo.taskgroup.QueueTaskGroup
import com.example.taskdemo.taskgroup.ScheduledTaskGroup
import org.springframework.stereotype.Service


@Service
class TaskService {

    private val scheduledTaskGroup = ScheduledTaskGroup()
    private val queueTaskGroup = QueueTaskGroup()

    fun runSchedule(task: Task, context: TaskContext, config: TaskConfig) {
        scheduledTaskGroup.addTask(task, context, config)
    }

    fun runQueue(task: Task) {
        queueTaskGroup.addTask(task)
    }

    fun runDaemon(task: Task) {

    }

    fun removeTask(task: Task) {
        scheduledTaskGroup.removeTask(task)
        queueTaskGroup.removeTask(task)
    }

    fun stopQueue() {
        queueTaskGroup.stop()
    }

    fun startQueue() {
        queueTaskGroup.start()
    }

    fun stopSchedule() {
        scheduledTaskGroup.stop()
    }

    fun startSchedule() {
        scheduledTaskGroup.start()
    }
}
