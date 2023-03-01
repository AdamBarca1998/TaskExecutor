package com.example.taskdemo.taskgroup

import com.example.taskdemo.AppVars
import com.example.taskdemo.model.Task
import com.example.taskdemo.model.TaskConfig
import com.example.taskdemo.service.QueueTaskService
import com.example.taskdemo.service.TaskLockService
import java.time.Duration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class QueueTaskGroup(
    private val queueTaskService: QueueTaskService,
    private val taskLockService: TaskLockService
) : SerializedTaskGroup() {

    private val appVars = AppVars()

    init {
        // locker
        scope.launch(Dispatchers.IO) {
            while (true) {
                try {
                    val expiredTasks = queueTaskService.findExpired(EXPIRED_LOCK_TIME_M, appVars.appId)

                    expiredTasks.forEach {
                        plannedTasks.add(TaskWithConfig(it, TaskConfig.Builder().build()))
                    }
                } catch (e: Exception) {
                    logger.error { e }
                } finally {
                    delay(/*getNextRefreshMillis()*/Duration.ofSeconds(30).toMillis())
                }
            }
        }
    }

    override fun isEnable(task: Task): Boolean = true

    override fun addTask(task: Task, taskConfig: TaskConfig) {
        queueTaskService.saveTask(task)
    }
}