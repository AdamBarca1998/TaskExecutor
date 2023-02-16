package com.example.taskdemo.service

import com.example.taskdemo.model.entities.TaskEntity
import com.example.taskdemo.repository.TaskRepository
import org.springframework.stereotype.Service

@Service
class TaskService(
    private val taskRepository: TaskRepository
) {

    fun createTask(taskEntity: TaskEntity) = taskRepository.insert(taskEntity)

    fun isEnableByClazz(clazz: String) = taskRepository.isEnableByClazz(clazz)
}