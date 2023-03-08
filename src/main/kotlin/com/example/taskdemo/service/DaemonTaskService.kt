package com.example.taskdemo.service

import com.example.taskdemo.mappers.DaemonTaskMapper
import com.example.taskdemo.model.Task
import com.example.taskdemo.model.entities.TaskLockEntity
import com.example.taskdemo.repository.DaemonTaskRepository
import org.springframework.stereotype.Service

@Service
class DaemonTaskService(
    private val daemonTaskRepository: DaemonTaskRepository,
    private val daemonTaskMapper: DaemonTaskMapper
) {

    fun createTask(task: Task, scheduleLock: TaskLockEntity) {
        daemonTaskRepository.insert(daemonTaskMapper.toEntity(task, scheduleLock))
    }

    fun isEnableByClazzPath(clazzPath: String) = daemonTaskRepository.isEnableByClazzPath(clazzPath)
}