package com.example.taskdemo.service

import com.example.taskdemo.enums.ScheduleTaskType
import com.example.taskdemo.model.Task
import com.example.taskdemo.model.TaskConfig
import com.example.taskdemo.taskgroup.DaemonTaskGroup
import com.example.taskdemo.taskgroup.EveryScheduleTaskGroup
import com.example.taskdemo.taskgroup.QueueTaskGroup
import com.example.taskdemo.taskgroup.ScheduleTaskGroup
import org.springframework.stereotype.Service

@Service
class TaskGroupService(
    private val scheduleTaskService: ScheduleTaskService,
    private val queueTaskService: QueueTaskService,
    taskLockService: TaskLockService,
    private val daemonTaskService: DaemonTaskService,
    taskLogService: TaskLogService
) {

    private val scheduledTaskGroup = ScheduleTaskGroup(taskLockService, scheduleTaskService, taskLogService)
    private val queueTaskGroup = QueueTaskGroup(queueTaskService, taskLogService)
    private val daemonTaskGroup = DaemonTaskGroup(taskLockService, daemonTaskService, taskLogService)
    private val heavyScheduledTaskGroup = ScheduleTaskGroup(taskLockService, scheduleTaskService, taskLogService)
    private val everyScheduleTaskGroup = EveryScheduleTaskGroup(taskLogService)

    // add
    fun addQueue(task: Task) {
        queueTaskGroup.addTask(task)
    }

    fun addSchedule(task: Task, config: TaskConfig) {
        when(config.type) {
            ScheduleTaskType.HEAVY -> heavyScheduledTaskGroup.addTask(task, config)
            ScheduleTaskType.EVERY -> everyScheduleTaskGroup.addTask(task, config)
            ScheduleTaskType.NORMAL -> scheduledTaskGroup.addTask(task, config)
            else -> throw IllegalStateException("${config.type} not defined!")
        }
    }

    fun addDaemon(task: Task) {
        daemonTaskGroup.addTask(task)
    }

    // get all
    fun getAllQueues() = queueTaskService.findAll()

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

    // restart
    fun restart() = queueTaskGroup.restart()

    fun eraserUselessTasks() {
        scheduleTaskService.deleteAllByClazzPathNotIn(getAllSchedules().stream()
            .map { it.second }
            .toList())
        daemonTaskService.deleteAllByClazzPathNotIn(getAllDaemons().stream()
            .map { it.second }
            .toList())
    }
}
