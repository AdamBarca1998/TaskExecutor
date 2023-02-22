package com.example.taskdemo.service

import com.example.taskdemo.model.Task
import com.example.taskdemo.model.TaskConfig
import com.example.taskdemo.taskgroup.DaemonTaskGroup
import com.example.taskdemo.taskgroup.QueueTaskGroup
import com.example.taskdemo.taskgroup.ScheduleTaskGroup
import org.springframework.stereotype.Service

@Service
class TaskGroupService(
    scheduleTaskService: ScheduleTaskService,
    taskLockService: TaskLockService
) {

    private val scheduledTaskGroup = ScheduleTaskGroup(taskLockService, scheduleTaskService)
    private val queueTaskGroup = QueueTaskGroup(scheduleTaskService)
    private val daemonTaskGroup = DaemonTaskGroup(scheduleTaskService)
    private val heavyScheduledTaskGroup = ScheduleTaskGroup(taskLockService, scheduleTaskService)

    fun addSchedule(task: Task, config: TaskConfig) {
        if (config.heavy) {
            heavyScheduledTaskGroup.addTask(task, config)
        } else {
            scheduledTaskGroup.addTask(task, config)
        }
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
