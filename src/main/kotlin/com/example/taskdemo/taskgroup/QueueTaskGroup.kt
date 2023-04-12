package com.example.taskdemo.taskgroup

import com.example.taskdemo.enums.TaskState
import com.example.taskdemo.model.Task
import com.example.taskdemo.model.TaskConfig
import com.example.taskdemo.model.TaskContext
import com.example.taskdemo.model.entities.TaskLogEntity
import com.example.taskdemo.service.QueueTaskService
import com.example.taskdemo.service.TaskLogService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class QueueTaskGroup(
    private val queueTaskService: QueueTaskService,
    taskLogService: TaskLogService
) : TaskGroup(taskLogService) {

    private var locker: Job = launchNewLocker()

    fun restart() {
        locker.cancel()
        locker = launchNewLocker()
    }

    override fun createLog(task: Task): TaskLogEntity {
        return TaskLogEntity().also {
            it.queueTaskId = task.id
        }
    }

    override fun isEnable(task: Task): Boolean = true

    override fun handleRun(task: Task): TaskLogEntity {
        queueTaskService.updateStateById(task.id, TaskState.RUNNING)
        return super.handleRun(task)
    }

    override fun handleError(task: Task, taskLogEntity: TaskLogEntity?, e: Exception) {
        super.handleError(task, taskLogEntity, e)
        queueTaskService.updateStateById(task.id, TaskState.ERROR)
    }

    override fun handleFinish(task: Task, taskLogEntity: TaskLogEntity?) {
        super.handleFinish(task, taskLogEntity)
        queueTaskService.updateStateById(task.id, TaskState.FINISHED)
    }

    override fun handleCancel(task: Task, taskLogEntity: TaskLogEntity?) {
        super.handleCancel(task, taskLogEntity)
        queueTaskService.updateStateById(task.id, TaskState.CANCELED)
    }

    override fun addTask(task: Task, taskConfig: TaskConfig) {
        queueTaskService.saveTask(task)
    }

    override suspend fun planNextExecution(taskStruct: TaskStruct, taskContext: TaskContext) {
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
                            queueTaskService.refreshLockByTaskId(runningTasks.peek().taskStruct.task.id)
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
            plannedTasks.add(TaskStruct(it, TaskConfig.Builder().build()))
            queueTaskService.updateStateById(it.id, TaskState.PLANNED)
        }
    }

    override fun planNextTaskById(id: Long) {
        val task = queueTaskService.findById(id)
        queueTaskService.refreshLockByTaskId(id)

        plannedTasks.add(TaskStruct(task, TaskConfig.Builder().build()))
        queueTaskService.updateStateById(task.id, TaskState.PLANNED)
    }
}