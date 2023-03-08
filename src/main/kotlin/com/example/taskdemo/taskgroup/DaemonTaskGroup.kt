package com.example.taskdemo.taskgroup

import com.example.taskdemo.model.DaemonTaskContext
import com.example.taskdemo.model.Task
import com.example.taskdemo.model.TaskConfig
import com.example.taskdemo.model.entities.TaskLockEntity
import com.example.taskdemo.service.DaemonTaskService
import com.example.taskdemo.service.TaskLockService
import java.time.ZonedDateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.hibernate.exception.ConstraintViolationException
import org.springframework.dao.DataIntegrityViolationException

class DaemonTaskGroup(
    private val taskLockService: TaskLockService,
    private val daemonTaskService: DaemonTaskService
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
                            // async run
                            savedTasks.forEach {
                                val job = launch { runTask(it) }

                                runningTasks.add(TaskWithJob(it, job))
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

    override fun planNextExecution(taskWithConfig: TaskWithConfig,
                                    lastExecution: ZonedDateTime,
                                    lastCompletion: ZonedDateTime
    ) {
        val context = DaemonTaskContext(taskWithConfig.taskConfig.startDateTime, lastExecution, lastCompletion)

        context.nextExecution().let {
            taskWithConfig.taskConfig.startDateTime = it ?: ZonedDateTime.now().plusDays(1L)
            plannedTasks.add(taskWithConfig)
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