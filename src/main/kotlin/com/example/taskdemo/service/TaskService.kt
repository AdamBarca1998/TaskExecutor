package com.example.taskdemo.service

import com.example.taskdemo.model.Task
import com.example.taskdemo.model.TaskConfig
import com.example.taskdemo.taskgroup.DaemonTaskGroup
import com.example.taskdemo.taskgroup.QueueTaskGroup
import com.example.taskdemo.taskgroup.ScheduledTaskGroup
import org.springframework.stereotype.Service


@Service
class TaskService {

    private val scheduledTaskGroup = ScheduledTaskGroup()
    private val queueTaskGroup = QueueTaskGroup()
    private val daemonTaskGroup = DaemonTaskGroup()

    fun runSchedule(task: Task, config: TaskConfig) {
        scheduledTaskGroup.addTask(task, config)
    }

    fun runQueue(task: Task) {
        queueTaskGroup.addTask(task)
    }

    fun runDaemon(task: Task, config: TaskConfig) {
        daemonTaskGroup.addTask(task, config)
    }

    fun removeTask(task: Task) {
        scheduledTaskGroup.removeTask(task)
        queueTaskGroup.removeTask(task)
        daemonTaskGroup.removeTask(task)
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

    fun stopDaemon() {
        daemonTaskGroup.stop()
    }

    fun startDaemon() {
        daemonTaskGroup.start()
    }
}
