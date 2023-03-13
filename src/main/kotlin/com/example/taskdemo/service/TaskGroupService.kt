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
    queueTaskService: QueueTaskService,
    taskLockService: TaskLockService,
    daemonTaskService: DaemonTaskService,
    taskContextService: TaskContextService
) {

    private val scheduledTaskGroup = ScheduleTaskGroup(taskLockService, scheduleTaskService)
    private val queueTaskGroup = QueueTaskGroup(queueTaskService)
    private val daemonTaskGroup = DaemonTaskGroup(taskLockService, daemonTaskService, taskContextService)
    private val heavyScheduledTaskGroup = ScheduleTaskGroup(taskLockService, scheduleTaskService)

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

    fun stopQueue() {
        queueTaskGroup.stopGroup()
    }

    fun stopSchedule() {
        scheduledTaskGroup.stopGroup()
        heavyScheduledTaskGroup.stopGroup()
    }

    fun stopDaemon() {
        daemonTaskGroup.stopGroup()
    }

    fun getAllDaemons() = daemonTaskGroup.getAllClazzPath()

    fun cancelDaemonByClazzPath(clazzPath: String) = daemonTaskGroup.removeTaskByClazzPath(clazzPath)

    fun getAllSchedules() = scheduledTaskGroup.getAllClazzPath() + heavyScheduledTaskGroup.getAllClazzPath()

    fun cancelScheduleByClazzPath(clazzPath: String) {
        scheduledTaskGroup.removeTaskByClazzPath(clazzPath)
        heavyScheduledTaskGroup.removeTaskByClazzPath(clazzPath)
    }

    fun cancelQueueTaskById(id: Long) = queueTaskGroup.removeTaskById(id)
}
