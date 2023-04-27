package com.example.taskdemo.taskgroup

import com.example.taskdemo.model.Task
import com.example.taskdemo.model.TaskConfig
import com.example.taskdemo.model.entities.TaskContext
import com.example.taskdemo.model.entities.TaskLockEntity
import com.example.taskdemo.model.entities.TaskLogEntity
import com.example.taskdemo.service.DaemonTaskService
import com.example.taskdemo.service.TaskLockService
import com.example.taskdemo.service.TaskLogService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DaemonTaskGroup(
    private val taskLockService: TaskLockService,
    private val daemonTaskService: DaemonTaskService,
    taskLogService: TaskLogService
) : TaskGroup(taskLogService) {

    private val lockName: String = "daemonGroup"
    private var daemonLock: TaskLockEntity = taskLockService.createIfNotExists(lockName)

    init {
        // locker
        scope.launch(Dispatchers.IO) {
            while (true) {
                try {
                    if (!isLocked.get()) {
                        if (taskLockService.tryRefreshLockByName(lockName, port)) {
                            daemonLock = taskLockService.findByName(lockName)
                        } else {
                            savedTasks.forEach {
                                cancelTaskById(it.task.id)
                            }
                        }

                        if (daemonLock.lockedBy == port && plannedTasks.isEmpty() && runningTasks.isEmpty()) {
                            val entities = daemonTaskService.findAll()
                            // async run
                            entities.forEach { entity ->
                                savedTasks.find { it.task.id == entity.id }?.let {
                                    it.taskContext = entity.taskContext

                                    val job = launch { startTask(it) }

                                    runningTasks.add(TaskWithJob(it, job))
                                }
                            }
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
            it.daemonTaskId = task.id
        }
    }

    override fun isEnable(task: Task) = daemonTaskService.isEnableById(task.id)

    override suspend fun planNextExecution(taskStruct: TaskStruct) {
        daemonTaskService.updateContextById(taskStruct.task.id, taskStruct.taskContext)
        runningTasks.find { it.taskStruct.task.id == taskStruct.task.id }?.let {
            plannedTasks.add(it.taskStruct)
        }
    }

    override fun addTask(task: Task, taskConfig: TaskConfig) {
        val taskStruct = TaskStruct(task, taskConfig,
            TaskContext(null, null, taskConfig.startDateTime)
        )

        task.id = daemonTaskService.createIfNotExists(task, daemonLock, taskStruct.taskContext)

        savedTasks.add(taskStruct)
        if (daemonLock.lockedBy == port) {
            plannedTasks.add(taskStruct)
            runNextTask()
        }
    }

    override fun runNextTask(runType: RunType) {
        super.runNextTask(RunType.TASK_GROUP)
    }
}