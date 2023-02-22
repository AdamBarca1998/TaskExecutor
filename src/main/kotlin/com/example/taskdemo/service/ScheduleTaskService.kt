package com.example.taskdemo.service

import com.example.taskdemo.model.entities.ScheduleTaskEntity
import com.example.taskdemo.repository.ScheduleTaskRepository
import org.springframework.stereotype.Service

@Service
class ScheduleTaskService(
    private val scheduleTaskRepository: ScheduleTaskRepository
) {

    fun createTask(scheduleTaskEntity: ScheduleTaskEntity) = scheduleTaskRepository.insert(scheduleTaskEntity)

    fun isEnableByClazz(clazz: String) = scheduleTaskRepository.isEnableByClazz(clazz)
}