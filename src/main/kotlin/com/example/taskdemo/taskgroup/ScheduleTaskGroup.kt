package com.example.taskdemo.taskgroup

import com.example.taskdemo.model.Task
import com.example.taskdemo.model.TaskConfig
import com.example.taskdemo.model.TaskContext
import com.example.taskdemo.model.entities.TaskLockEntity
import com.example.taskdemo.model.entities.TaskLogEntity
import com.example.taskdemo.service.ScheduleTaskService
import com.example.taskdemo.service.TaskContextService
import com.example.taskdemo.service.TaskLockService
import com.example.taskdemo.service.TaskLogService
import java.time.ZonedDateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ScheduleTaskGroup(
    private val taskLockService: TaskLockService,
    private val scheduleTaskService: ScheduleTaskService,
    private val taskContextService: TaskContextService,
    taskLogService: TaskLogService
) : TaskGroup(taskLogService) {

    private val lockName: String = "scheduleGroup"
    private var scheduleLock: TaskLockEntity = taskLockService.createIfNotExists(lockName)

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
                            val entities = scheduleTaskService.findAll()

                            entities.forEach { entity ->
                                savedTasks.find { it.task.id == entity.id }?.let {
                                    it.taskConfig.startDateTime = entity.taskContext.startDateTime
                                    plannedTasks.add(it)
                                }
                            }

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

    override fun createLog(task: Task): TaskLogEntity {
        return TaskLogEntity().also {
            it.scheduleTaskId = task.id
        }
    }


    override fun isEnable(task: Task) = scheduleTaskService.isEnableById(task.id)

    override fun addTask(task: Task, taskConfig: TaskConfig) {
        val taskStruct = TaskStruct(task, taskConfig)

        task.id = scheduleTaskService.createIfNotExists(task, scheduleLock)

        savedTasks.add(taskStruct)
        if (scheduleLock.lockedBy == port) {
            plannedTasks.add(taskStruct)
            if (runningTasks.isEmpty()) {
                runNextTask()
            }
        }
    }

    override suspend fun planNextExecution(taskStruct: TaskStruct, taskContext: TaskContext) {
        val newContext = TaskContext(
            taskStruct.taskConfig.nextExecution(taskContext) ?: ZonedDateTime.now().plusDays(1),
            taskContext.lastExecution,
            taskContext.lastCompletion,
            null
        )

        taskContextService.updateByScheduleId(newContext, taskStruct.task.id)
        runningTasks.find { it.taskStruct.task.id == taskStruct.task.id }?.let {
            taskStruct.taskConfig.startDateTime = newContext.startDateTime
            plannedTasks.add(it.taskStruct)
        }
    }
}