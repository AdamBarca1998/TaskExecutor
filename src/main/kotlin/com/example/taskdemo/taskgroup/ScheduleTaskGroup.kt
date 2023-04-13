package com.example.taskdemo.taskgroup

import com.example.taskdemo.model.Task
import com.example.taskdemo.model.TaskConfig
import com.example.taskdemo.model.entities.TaskLockEntity
import com.example.taskdemo.model.entities.TaskLogEntity
import com.example.taskdemo.service.ScheduleTaskService
import com.example.taskdemo.service.TaskLockService
import com.example.taskdemo.service.TaskLogService
import java.time.ZonedDateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ScheduleTaskGroup(
    private val taskLockService: TaskLockService,
    private val scheduleTaskService: ScheduleTaskService,
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
                        } else {
                            runningTasks.clear()
                            plannedTasks.clear()
                        }

                        if (scheduleLock.lockedBy == port && plannedTasks.isEmpty() && runningTasks.isEmpty()) {
                            val entities = scheduleTaskService.findAll()

                            entities.forEach { entity ->
                                savedTasks.find { it.task.id == entity.id }?.let {
                                    it.taskContext = entity.taskContext
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

    override suspend fun planNextExecution(taskStruct: TaskStruct) {
        taskStruct.taskContext.nextExecution = taskStruct.taskConfig.nextExecution(taskStruct.taskContext) ?: ZonedDateTime.now().plusDays(1)

        scheduleTaskService.updateContextById(taskStruct.task.id, taskStruct.taskContext)
        runningTasks.find { it.taskStruct.task.id == taskStruct.task.id }?.let {
            plannedTasks.add(it.taskStruct)
        }
    }
}