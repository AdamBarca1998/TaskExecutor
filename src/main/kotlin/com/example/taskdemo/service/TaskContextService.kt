package com.example.taskdemo.service

import com.example.taskdemo.mappers.TaskContextMapper
import com.example.taskdemo.model.TaskContext
import com.example.taskdemo.repository.TaskContextRepository
import org.springframework.stereotype.Service

@Service
class TaskContextService(
    private val taskContextRepository: TaskContextRepository,
    private val taskContextMapper: TaskContextMapper
) {

    fun updateByClazzPath(taskContext: TaskContext, clazzPath: String): Boolean {
        return taskContextRepository.updateByClazzPath(taskContextMapper.toEntity(taskContext), clazzPath) > 0
    }
}