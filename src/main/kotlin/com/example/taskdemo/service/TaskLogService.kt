package com.example.taskdemo.service

import com.example.taskdemo.model.entities.TaskLogEntity
import com.example.taskdemo.repository.TaskLogRepository
import org.springframework.stereotype.Service

@Service
class TaskLogService(
    private val taskLogRepository: TaskLogRepository
) {

    fun save(taskLockEntity: TaskLogEntity) = taskLogRepository.save(taskLockEntity)
}