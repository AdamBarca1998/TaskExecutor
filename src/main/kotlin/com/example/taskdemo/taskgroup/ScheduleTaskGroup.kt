package com.example.taskdemo.taskgroup

import com.example.taskdemo.mappers.ScheduleTaskMapper
import com.example.taskdemo.model.Task
import com.example.taskdemo.model.TaskConfig
import com.example.taskdemo.model.entities.TaskLockEntity
import com.example.taskdemo.service.TaskLockService
import com.example.taskdemo.service.ScheduleTaskService
import java.time.Duration
import kotlin.random.Random
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.hibernate.exception.ConstraintViolationException
import org.springframework.dao.DataIntegrityViolationException

class ScheduleTaskGroup(
    private val taskLockService: TaskLockService,
    private val scheduleTaskService: ScheduleTaskService,
    private val scheduleTaskMapper: ScheduleTaskMapper
) : SerializedTaskGroup(
    scheduleTaskService
) {

    private val appId = "8080" //TODO:DELETE

    override val name: String = "scheduleGroup"
    private var scheduleLock: TaskLockEntity = taskLockService.tryRefreshLockByName(name, EXPIRED_LOCK_TIME_M, appId)
    private val savedTasks: ArrayList<TaskWithConfig> = arrayListOf()

    init {
        // locker
        scope.launch(Dispatchers.IO) {
            while (true) {
                scheduleLock = taskLockService.tryRefreshLockByName(name, EXPIRED_LOCK_TIME_M, appId)

                if (scheduleLock.lockedBy == appId && plannedTasks.isEmpty() && runningTasks.isEmpty()) {
                    plannedTasks.addAll(savedTasks)
                }

                delay(
                    Duration.ofMinutes(
                        Random.nextLong(REFRESH_LOCK_TIME_M.toLong() / 2, REFRESH_LOCK_TIME_M.toLong() * 2)
                    ).toMillis()
                )
            }
        }
    }

    override fun addTask(task: Task, taskConfig: TaskConfig) {
        // try create
        try {
            scheduleTaskService.createTask(scheduleTaskMapper.toEntity(task, taskConfig, scheduleLock))
        } catch (e: DataIntegrityViolationException) {
            if ((e.cause as ConstraintViolationException).constraintName != "task_clazz_key") {
                throw e
            }
        }

        savedTasks.add(TaskWithConfig(task, taskConfig))
        if (scheduleLock.lockedBy == appId) {
            super.addTask(task, taskConfig)
        }
    }
}