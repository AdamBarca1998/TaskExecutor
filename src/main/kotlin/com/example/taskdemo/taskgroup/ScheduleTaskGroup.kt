package com.example.taskdemo.taskgroup

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
) : TaskGroup() {

    private val port = "8080" //TODO:DELETE
    private val lockName: String = "scheduleGroup"
    private var scheduleLock: TaskLockEntity = taskLockService.findByName(lockName)
    private val savedTasks: ArrayList<TaskWithConfig> = arrayListOf()

    init {
        // locker
        scope.launch(Dispatchers.IO) {
            while (true) {
                try {
                    if (!isLocked.get()) {
                        if (taskLockService.tryRefreshLockByName(lockName, port)) {
                            scheduleLock = taskLockService.findByName(lockName)
                        }

                        if (scheduleLock.lockedBy == port && plannedTasks.isEmpty() && runningTasks.isEmpty()) {
                            plannedTasks.addAll(savedTasks)
                            runNextTask()
                        }
                    }
                } catch (e: Exception) {
                    logger.error { e }
                } finally {
                    delay(getNextRefreshMillis())
                }
            }
        }
    }

    override fun isEnable(task: Task) = scheduleTaskService.isEnableByClazzPath(task.javaClass.name)

    override fun addTask(task: Task, taskConfig: TaskConfig) {
        val taskWithConfig = TaskWithConfig(task, taskConfig)

        // try create
        try {
            scheduleTaskService.createTask(task, scheduleLock)
        } catch (e: DataIntegrityViolationException) {
            if ((e.cause as ConstraintViolationException).constraintName != "schedule_task_clazz_path_key") {
                throw e
            }
        }

        savedTasks.add(taskWithConfig)
        if (scheduleLock.lockedBy == port) {
            plannedTasks.add(taskWithConfig)
            if (runningTasks.isEmpty()) {
                runNextTask()
            }
        }
    }
}