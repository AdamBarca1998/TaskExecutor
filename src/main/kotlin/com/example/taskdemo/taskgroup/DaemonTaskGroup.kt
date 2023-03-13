package com.example.taskdemo.taskgroup

import com.example.taskdemo.model.Task
import com.example.taskdemo.model.TaskConfig
import com.example.taskdemo.model.TaskContext
import com.example.taskdemo.model.entities.TaskLockEntity
import com.example.taskdemo.service.DaemonTaskService
import com.example.taskdemo.service.TaskContextService
import com.example.taskdemo.service.TaskLockService
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.hibernate.exception.ConstraintViolationException
import org.springframework.dao.DataIntegrityViolationException

class DaemonTaskGroup(
    private val taskLockService: TaskLockService,
    private val daemonTaskService: DaemonTaskService,
    private val taskContextService: TaskContextService
) : TaskGroup() {

    private val port = "8080" //TODO:DELETE
    private val lockName: String = "daemonGroup"
    private var daemonLock: TaskLockEntity = taskLockService.findByName(lockName)
    private val savedTasks: ArrayList<TaskWithConfig> = arrayListOf()

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

    fun getAll(): List<String> {
        val plannedTaskIds = plannedTasks.toList().stream()
            .map { it.task.javaClass.name }
            .toList()
        val runningTaskIds = runningTasks.toList().stream()
            .map { it.taskWithConfig.task.javaClass.name }
            .toList()

        return plannedTaskIds + runningTaskIds
    }

    fun removeTaskByClazzPath(clazzPath: String) {
        savedTasks.removeIf { it.task.javaClass.name == clazzPath }
        plannedTasks.removeIf { it.task.javaClass.name == clazzPath }
        runningTasks.find { it.taskWithConfig.task.javaClass.name == clazzPath }?.let {
            runningTasks.remove(it)
            it.job.cancel()
        }
    }


    override fun isEnable(task: Task) = daemonTaskService.isEnableByClazzPath(task.javaClass.name)

    override suspend fun planNextExecution(taskWithConfig: TaskWithConfig, taskContext: TaskContext) {
        val newContext = TaskContext(
            taskContext.nextExecution ?: Instant.now().plus(1, ChronoUnit.DAYS),
            taskContext.lastExecution,
            taskContext.lastCompletion,
            null
        )
        taskWithConfig.taskConfig.startDateTime = newContext.startDateTime
        taskContextService.updateByClazzPath(newContext, taskWithConfig.task.javaClass.name)
        savedTasks.find { it == taskWithConfig }?.let {
            plannedTasks.add(it)
        }
    }

    override fun addTask(task: Task, taskConfig: TaskConfig) {
        val taskWithConfig = TaskWithConfig(task, taskConfig)

        // try create
        try {
            daemonTaskService.createTask(task, daemonLock)
        } catch (e: DataIntegrityViolationException) {
            if ((e.cause as ConstraintViolationException).constraintName != "daemon_task_clazz_path_key") {
                throw e
            }
        }

        savedTasks.add(taskWithConfig)
        if (daemonLock.lockedBy == port) {
            plannedTasks.add(taskWithConfig)
            runNextTask()
        }
    }
}