package com.example.taskdemo.taskgroup

import com.example.taskdemo.enums.QueueTaskState
import com.example.taskdemo.model.Task
import com.example.taskdemo.model.TaskConfig
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

    fun removeTaskById(id: Long) {
        plannedTasks.removeIf { it.task.id == id }
        runningTasks.find { it.taskWithConfig.task.id == id }?.let {
            runningTasks.remove(it)
            it.job.cancel()
        }
        queueTaskService.updateStateById(id, QueueTaskState.CANCELED)
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

    override fun addTask(task: Task, taskConfig: TaskConfig) {
        queueTaskService.saveTask(task)
    }

    private fun getAllPlannedAndRunningTaskIds(): List<Long> {
        val plannedTaskIds = plannedTasks.toList().stream()
            .map { it.task.id }
            .toList()
        val runningTaskIds = runningTasks.toList().stream()
            .map { it.taskWithConfig.task.id }
            .toList()

        return plannedTaskIds + runningTaskIds
    }

    private fun launchNewLocker(): Job {
        return scope.launch(Dispatchers.IO) {
            while (true) {
                try {
                    if (!isLocked.get()) {
                        queueTaskService.refreshLocks(getAllPlannedAndRunningTaskIds())
                        val expiredTasks = queueTaskService.findExpired(port)

                        expiredTasks.forEach {
                            queueTaskService.updateStateById(it.id, QueueTaskState.PLANNED)
                            plannedTasks.add(TaskWithConfig(it, TaskConfig.Builder().build()))
                        }

                        if (runningTasks.isEmpty()) {
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
}