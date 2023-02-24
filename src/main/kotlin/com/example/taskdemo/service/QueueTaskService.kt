package com.example.taskdemo.service

import com.example.taskdemo.mappers.QueueTaskMapper
import com.example.taskdemo.model.Task
import com.example.taskdemo.repository.QueueTaskRepository
import org.springframework.stereotype.Service

@Service
class QueueTaskService(
    private val queueTaskRepository: QueueTaskRepository,
    private val queueTaskMapper: QueueTaskMapper
) {

    fun createTask(task: Task) {
        queueTaskRepository.save(queueTaskMapper.toEntity(task))
    }
}