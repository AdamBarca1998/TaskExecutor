package com.example.taskdemo.service

import com.example.taskdemo.mappers.ScheduleTaskMapper
import com.example.taskdemo.model.Task
import com.example.taskdemo.model.TaskConfig
import com.example.taskdemo.model.entities.TaskLockEntity
import com.example.taskdemo.repository.ScheduleTaskRepository
import org.springframework.stereotype.Service

@Service
class ScheduleTaskService(
    private val scheduleTaskRepository: ScheduleTaskRepository,
    private val scheduleTaskMapper: ScheduleTaskMapper
) {

    fun createTask(task: Task, taskConfig: TaskConfig, scheduleLock: TaskLockEntity) {
        scheduleTaskRepository.insert(scheduleTaskMapper.toEntity(task, taskConfig, scheduleLock))
    }

    fun isEnableByClazzPath(clazzPath: String) = scheduleTaskRepository.isEnableByClazzPath(clazzPath)
}