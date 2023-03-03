package com.example.taskdemo.taskgroup

import com.example.taskdemo.enums.QueueTaskState
import com.example.taskdemo.model.Task
import com.example.taskdemo.model.TaskConfig
import com.example.taskdemo.service.QueueTaskService
import java.time.ZonedDateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class QueueTaskGroup(
    private val queueTaskService: QueueTaskService
) : TaskGroup() {

    private val port = "8082" //TODO:DELETE

    init {
        // locker
        scope.launch(Dispatchers.IO) {
            while (true) {
                try {
                    queueTaskService.refreshLocks(getAllPlannedAndRunningTaskIds())
                    val expiredTasks = queueTaskService.findExpired(port)

                    expiredTasks.forEach {
                        queueTaskService.updateState(it, QueueTaskState.PLANNED)
                        plannedTasks.add(TaskWithConfig(it, TaskConfig.Builder().build()))
                    }

                    if (runningTasks.isEmpty()) {
                        planNextTask()
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
        queueTaskService.updateStateAndResult(task, QueueTaskState.ERROR, e.message ?: "Default value: handleError()")
    }

    override fun handleFinish(task: Task) {
        super.handleFinish(task)
        queueTaskService.updateState(task, QueueTaskState.FINISHED)
    }

    override fun addTask(task: Task, taskConfig: TaskConfig) {
        queueTaskService.saveTask(task)
    }

    override fun planNextExecution(taskWithConfig: TaskWithConfig, lastExecution: ZonedDateTime, lastCompletion: ZonedDateTime) {
        planNextTask()
    }

    private fun planNextTask() {
        plannedTasks.poll()?.let {
            val job = scope.launch { runTask(it) }

            runningTasks.add(TaskWithJob(it, job))
        }
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
}