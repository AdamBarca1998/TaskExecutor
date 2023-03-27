package com.example.taskdemo.taskgroup

import com.example.taskdemo.model.Task
import com.example.taskdemo.model.TaskConfig
import com.example.taskdemo.model.TaskContext
import com.example.taskdemo.model.entities.TaskLockEntity
import com.example.taskdemo.service.DaemonTaskService
import com.example.taskdemo.service.TaskContextService
import com.example.taskdemo.service.TaskLockService
import java.time.ZonedDateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DaemonTaskGroup(
    private val taskLockService: TaskLockService,
    private val daemonTaskService: DaemonTaskService,
    private val taskContextService: TaskContextService
) : TaskGroup() {

    private val lockName: String = "daemonGroup"
    private var daemonLock: TaskLockEntity = taskLockService.findByName(lockName)

    init {
        // locker
        scope.launch(Dispatchers.IO) {
            while (true) {
                try {
                    if (!isLocked.get()) {
                        if (taskLockService.tryRefreshLockByName(lockName, port)) {
                            daemonLock = taskLockService.findByName(lockName)
                        }

                        if (daemonLock.lockedBy == port && plannedTasks.isEmpty() && runningTasks.isEmpty()) {
                            val entities = daemonTaskService.findAll()
                            // async run
                            entities.forEach { entity ->
                                savedTasks.find { it.task.javaClass.name == entity.clazzPath }?.let {
                                    it.taskConfig.startDateTime = entity.taskContext.startDateTime

                                    val job = launch { runTask(it) }

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

    override fun isEnable(task: Task) = daemonTaskService.isEnableByClazzPath(task.javaClass.name)

    override suspend fun planNextExecution(taskWithConfig: TaskWithConfig, taskContext: TaskContext) {
        val newContext = TaskContext(
            taskContext.nextExecution ?: ZonedDateTime.now().plusDays(1),
            taskContext.lastExecution,
            taskContext.lastCompletion,
            null
        )

        taskContextService.updateByClazzPath(newContext, taskWithConfig.task.javaClass.name)
        runningTasks.find { it.taskWithConfig.task.id == taskWithConfig.task.id }?.let {
            taskWithConfig.taskConfig.startDateTime = newContext.startDateTime
            plannedTasks.add(it.taskWithConfig)
        }
    }

    override fun addTask(task: Task, taskConfig: TaskConfig) {
        val taskWithConfig = TaskWithConfig(task, taskConfig)

        task.id = daemonTaskService.createIfNotExists(task, daemonLock)

        savedTasks.add(taskWithConfig)
        if (daemonLock.lockedBy == port) {
            plannedTasks.add(taskWithConfig)
            runNextTask()
        }
    }

    override fun runNextTask(runType: RunType) {
        super.runNextTask(RunType.TASK_GROUP)
    }
}