package com.example.taskdemo.taskgroup

import com.example.taskdemo.model.Task
import com.example.taskdemo.model.TaskConfig
import com.example.taskdemo.model.TaskContext
import com.example.taskdemo.model.entities.TaskLockEntity
import com.example.taskdemo.service.ScheduleTaskService
import com.example.taskdemo.service.TaskLockService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ScheduleTaskGroup(
    private val taskLockService: TaskLockService,
    private val scheduleTaskService: ScheduleTaskService
) : TaskGroup() {

    private val lockName: String = "scheduleGroup"
    private var scheduleLock: TaskLockEntity = taskLockService.findByName(lockName)

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

        task.id = scheduleTaskService.createIfNotExists(task, scheduleLock)

        savedTasks.add(taskWithConfig)
        if (scheduleLock.lockedBy == port) {
            plannedTasks.add(taskWithConfig)
            if (runningTasks.isEmpty()) {
                runNextTask()
            }
        }
    }

    override suspend fun planNextExecution(taskWithConfig: TaskWithConfig, taskContext: TaskContext) {
        runningTasks.find { it.taskWithConfig.task.id == taskWithConfig.task.id }?.let {
            taskWithConfig.taskConfig.nextExecution(taskContext)?.let { nextTime ->
                taskWithConfig.taskConfig.startDateTime = nextTime
                plannedTasks.add(TaskWithConfig(taskWithConfig.task, taskWithConfig.taskConfig))
            }
        }
    }
}