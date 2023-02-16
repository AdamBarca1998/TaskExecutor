package com.example.taskdemo.service

import com.example.taskdemo.mappers.TaskMapper
import com.example.taskdemo.model.Task
import com.example.taskdemo.model.TaskConfig
import com.example.taskdemo.taskgroup.DaemonTaskGroup
import com.example.taskdemo.taskgroup.QueueTaskGroup
import com.example.taskdemo.taskgroup.ScheduleTaskGroup
import org.springframework.stereotype.Service

@Service
class TaskGroupService(
    taskService: TaskService,
    taskLockService: TaskLockService,
    taskMapper: TaskMapper
) {

    private val scheduledTaskGroup = ScheduleTaskGroup(taskLockService, taskService, taskMapper)
    private val queueTaskGroup = QueueTaskGroup(taskService)
    private val daemonTaskGroup = DaemonTaskGroup(taskService)
    private val heavyScheduledTaskGroup = ScheduleTaskGroup(taskLockService, taskService, taskMapper)

    fun addSchedule(task: Task, config: TaskConfig) {
        if (config.heavy) {
            heavyScheduledTaskGroup.addTask(task, config)
        } else {
            scheduledTaskGroup.addTask(task, config)
        }
    }

    fun addQueue(task: Task) {
        queueTaskGroup.addTask(task)
    }

    fun addDaemon(task: Task) {
        daemonTaskGroup.addTask(task)
    }

    fun removeTask(task: Task) {
        heavyScheduledTaskGroup.removeTask(task)
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
        heavyScheduledTaskGroup.stop()
    }

    fun startSchedule() {
        scheduledTaskGroup.start()
        heavyScheduledTaskGroup.start()
    }

    fun stopDaemon() {
        daemonTaskGroup.stop()
    }

    fun startDaemon() {
        daemonTaskGroup.start()
    }
}
