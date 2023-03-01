package com.example.taskdemo.taskgroup

import com.example.taskdemo.AppVars
import com.example.taskdemo.model.Task
import com.example.taskdemo.model.TaskConfig
import com.example.taskdemo.model.entities.TaskLockEntity
import com.example.taskdemo.service.ScheduleTaskService
import com.example.taskdemo.service.TaskLockService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.hibernate.exception.ConstraintViolationException
import org.springframework.dao.DataIntegrityViolationException

class ScheduleTaskGroup(
    private val taskLockService: TaskLockService,
    private val scheduleTaskService: ScheduleTaskService
) : SerializedTaskGroup() {

    private val appVars = AppVars()
    override val name: String = "scheduleGroup"
    private var scheduleLock: TaskLockEntity = taskLockService.tryRefreshLockByName(name, EXPIRED_LOCK_TIME_M, appVars.appId)
    private val savedTasks: ArrayList<TaskWithConfig> = arrayListOf()

    init {
        // locker
        scope.launch(Dispatchers.IO) {
            while (true) {
                try {
                    scheduleLock = taskLockService.tryRefreshLockByName(name, EXPIRED_LOCK_TIME_M, appVars.appId)

                    if (scheduleLock.lockedBy == appVars.appId && plannedTasks.isEmpty() && runningTasks.isEmpty()) {
                        plannedTasks.addAll(savedTasks)
                    }
                } catch (e: Exception) {
                    logger.error { e }
                } finally {
                    delay(getNextRefreshMillis())
                }
            }
        }
    }

    override fun isEnable(task: Task): Boolean {
        return scheduleTaskService.isEnableByClazzPath(task.javaClass.name)
    }

    override fun addTask(task: Task, taskConfig: TaskConfig) {
        // try create
        try {
            scheduleTaskService.createTask(task, taskConfig, scheduleLock)
        } catch (e: DataIntegrityViolationException) {
            if ((e.cause as ConstraintViolationException).constraintName != "schedule_task_clazz_path_key") {
                throw e
            }
        }

        savedTasks.add(TaskWithConfig(task, taskConfig))
        if (scheduleLock.lockedBy == appVars.appId) {
            super.addTask(task, taskConfig)
        }
    }
}