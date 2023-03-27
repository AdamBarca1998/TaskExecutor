package com.example.taskdemo.taskgroup

import com.example.taskdemo.enums.QueueTaskState
import com.example.taskdemo.model.Task
import com.example.taskdemo.model.TaskConfig
import com.example.taskdemo.model.TaskContext
import com.example.taskdemo.service.QueueTaskService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class QueueTaskGroup(
    private val queueTaskService: QueueTaskService
) : TaskGroup() {

    private var locker: Job = launchNewLocker()

    fun restart() {
        locker.cancel()
        locker = launchNewLocker()
    }

    override fun isEnable(task: Task): Boolean = true

    override fun handleRun(task: Task) {
        super.handleRun(task)
        queueTaskService.updateStateById(task.id, QueueTaskState.RUNNING)
    }

    override fun handleError(task: Task, e: Exception) {
        super.handleError(task, e)
        queueTaskService.updateStateAndResultById(task.id, QueueTaskState.ERROR, e.message ?: "Default value: handleError()")
    }

    override fun handleFinish(task: Task) {
        super.handleFinish(task)
        queueTaskService.updateStateById(task.id, QueueTaskState.FINISHED)
    }

    override fun handleCancel(id: Long) {
        super.handleCancel(id)
        queueTaskService.updateStateById(id, QueueTaskState.CANCELED)
    }

    override fun addTask(task: Task, taskConfig: TaskConfig) {
        queueTaskService.saveTask(task)
    }

    override suspend fun planNextExecution(taskWithConfig: TaskWithConfig, taskContext: TaskContext) {
        planNextTask()
    }

    private fun launchNewLocker(): Job {
        return scope.launch(Dispatchers.IO) {
            while (true) {
                try {
                    if (!isLocked.get()) {
                        if (runningTasks.isEmpty() && plannedTasks.isEmpty()) {
                            planNextTask()
                            runNextTask()
                        } else {
                            queueTaskService.refreshLockByTaskId(runningTasks.peek().taskWithConfig.task.id)
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

    private fun planNextTask() {
        queueTaskService.findOldestExpired(port)?.let {
            plannedTasks.add(TaskWithConfig(it, TaskConfig.Builder().build()))
            queueTaskService.updateStateById(it.id, QueueTaskState.PLANNED)
        }
    }

    override fun planNextTaskById(id: Long) {
        val task = queueTaskService.findById(id)

        plannedTasks.add(TaskWithConfig(task, TaskConfig.Builder().build()))
        queueTaskService.updateStateById(task.id, QueueTaskState.PLANNED)
    }
}