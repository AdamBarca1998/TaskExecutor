package com.example.taskdemo.service

import com.example.taskdemo.model.Task
import com.example.taskdemo.model.TaskConfig
import com.example.taskdemo.taskgroup.DaemonTaskGroup
import com.example.taskdemo.taskgroup.QueueTaskGroup
import com.example.taskdemo.taskgroup.ScheduleTaskGroup
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class TaskGroupService(
    scheduleTaskService: ScheduleTaskService,
    private val queueTaskService: QueueTaskService,
    taskLockService: TaskLockService,
    daemonTaskService: DaemonTaskService,
    taskLogService: TaskLogService
) {

    private val scheduledTaskGroup = ScheduleTaskGroup(taskLockService, scheduleTaskService, taskLogService)
    private val queueTaskGroup = QueueTaskGroup(queueTaskService, taskLogService)
    private val daemonTaskGroup = DaemonTaskGroup(taskLockService, daemonTaskService, taskLogService)
    private val heavyScheduledTaskGroup = ScheduleTaskGroup(taskLockService, scheduleTaskService, taskLogService)

    // add
    fun addQueue(task: Task) {
        queueTaskGroup.addTask(task)
    }

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

    // get all
    fun getAllQueues() = ResponseEntity.ok(queueTaskService.findAll())

    fun getAllSchedules() = scheduledTaskGroup.getAll() + heavyScheduledTaskGroup.getAll()

    fun getAllDaemons() = daemonTaskGroup.getAll()

    // start
    fun runQueueById(id: Long) = queueTaskGroup.runTaskById(id)

    fun runScheduleById(id: Long) {
        scheduledTaskGroup.runTaskById(id)
        heavyScheduledTaskGroup.runTaskById(id)
    }

    fun runDaemonById(id: Long) = daemonTaskGroup.runTaskById(id)

    // cancel
    fun cancelQueueTaskById(id: Long) = queueTaskGroup.cancelTaskById(id)

    fun cancelScheduleById(id: Long) {
        scheduledTaskGroup.cancelTaskById(id)
        heavyScheduledTaskGroup.cancelTaskById(id)
    }

    fun cancelDaemonById(id: Long) = daemonTaskGroup.cancelTaskById(id)

    // stop

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

    //

    fun restart() = queueTaskGroup.restart()
}
