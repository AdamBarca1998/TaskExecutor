package com.example.taskdemo.taskgroup

import com.example.taskdemo.extensions.toNullable
import com.example.taskdemo.mappers.TaskMapper
import com.example.taskdemo.model.Task
import com.example.taskdemo.model.TaskConfig
import com.example.taskdemo.service.TaskLockService
import com.example.taskdemo.service.TaskService
import org.hibernate.exception.ConstraintViolationException
import org.springframework.dao.DataIntegrityViolationException

private const val EXPIRED_TIME_M = 12
private const val REFRESH_TIME_M = 5
private const val APP_ID = "ID1"

class ScheduleTaskGroup(
    private val taskLockService: TaskLockService,
    private val taskService: TaskService,
    private val taskMapper: TaskMapper
) : QueueTaskGroup() {

    override val name: String = "scheduleGroup"
    private var scheduleLock = taskLockService.findByName("scheduleGroup")
    private val savedTasks: ArrayList<TaskWithConfig> = arrayListOf()

    init {
        taskLockService.findByName(name)?.let {
            if ( taskLockService.refreshLockById(it.id, EXPIRED_TIME_M, APP_ID) ) {
                scheduleLock = taskLockService.findById(it.id).toNullable()
            }
        }
    }

    override fun addTask(task: Task, taskConfig: TaskConfig) {
        // try create
        try {
            taskService.createTask(taskMapper.toEntity(task, taskConfig, scheduleLock))
        } catch (e: DataIntegrityViolationException) {
            if ((e.cause as ConstraintViolationException).constraintName != "task_clazz_key") {
                throw e
            }
        }

        savedTasks.add(TaskWithConfig(task, taskConfig))
        if (scheduleLock?.lockedBy == APP_ID) {
            super.addTask(task, taskConfig)
        }
    }
}