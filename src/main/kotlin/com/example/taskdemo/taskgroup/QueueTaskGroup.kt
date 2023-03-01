package com.example.taskdemo.taskgroup

import com.example.taskdemo.enums.QueueTaskState
import com.example.taskdemo.model.Task
import com.example.taskdemo.model.TaskConfig
import com.example.taskdemo.service.QueueTaskService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class QueueTaskGroup(
    private val queueTaskService: QueueTaskService
) : SerializedTaskGroup() {

    private val port = "8082" //TODO:DELETE

    init {
        // locker
        scope.launch(Dispatchers.IO) {
            while (true) {
                try {
                    val expiredTasks = queueTaskService.findExpired(EXPIRED_LOCK_TIME_M, port)

                    expiredTasks.forEach {
                        queueTaskService.updateState(it, QueueTaskState.PLANNED)
                        plannedTasks.add(TaskWithConfig(it, TaskConfig.Builder().build()))
                    }
                } catch (e: Exception) {
                    logger.error { e }
                } finally {
                    delay(getNextRefreshMillis())
                }
            }
        }
    }

    override fun isEnable(task: Task): Boolean = true

    override fun handleRun(task: Task) {
        super.handleRun(task)
        queueTaskService.updateState(task, QueueTaskState.RUNNING)
    }

    override fun handleError(task: Task, e: Exception) {
        super.handleError(task, e)
        queueTaskService.updateStateAndResult(task, QueueTaskState.ERROR, e.message ?: "Default value")
    }

    override fun handleFinish(task: Task) {
        super.handleFinish(task)
        queueTaskService.updateState(task, QueueTaskState.FINISHED)
    }

    override fun addTask(task: Task, taskConfig: TaskConfig) {
        queueTaskService.saveTask(task)
    }
}