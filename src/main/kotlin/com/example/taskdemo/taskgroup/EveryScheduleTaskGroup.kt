package com.example.taskdemo.taskgroup

import com.example.taskdemo.model.Task
import com.example.taskdemo.model.TaskConfig
import com.example.taskdemo.model.entities.TaskContext
import com.example.taskdemo.model.entities.TaskLogEntity
import com.example.taskdemo.service.MetricService
import com.example.taskdemo.service.TaskLogService
import java.time.ZonedDateTime

class EveryScheduleTaskGroup(
    taskLogService: TaskLogService,
    metricService: MetricService
) : TaskGroup(taskLogService, metricService) {

    override fun isEnable(task: Task): Boolean {
        return true
    }

    override fun createLog(task: Task): TaskLogEntity {
        return TaskLogEntity()
    }

    override fun handleRun(task: Task): TaskLogEntity {
        return createLog(task)
    }

    override fun handleError(task: Task, taskLogEntity: TaskLogEntity?, e: Exception) {}

    override fun handleFinish(task: Task, taskLogEntity: TaskLogEntity) {}

    override fun handleCancel(task: Task, taskLogEntity: TaskLogEntity?) {}

    override fun addTask(task: Task, taskConfig: TaskConfig) {
        val taskStruct = TaskStruct(task, taskConfig,
            TaskContext(null, null, taskConfig.startDateTime)
        )

        savedTasks.add(taskStruct)
        plannedTasks.add(taskStruct)
        runNextTask()
    }

    override suspend fun planNextExecution(taskStruct: TaskStruct) {
        taskStruct.taskContext.nextExecution = taskStruct.taskConfig.nextExecution(taskStruct.taskContext)
            ?: ZonedDateTime.now().plusDays(1)

        runningTasks.find { it.taskStruct.task.id == taskStruct.task.id }?.let {
            plannedTasks.add(it.taskStruct)
        }
    }
}