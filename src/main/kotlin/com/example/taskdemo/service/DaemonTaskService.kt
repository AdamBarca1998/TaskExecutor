package com.example.taskdemo.service

import com.example.taskdemo.mappers.DaemonTaskMapper
import com.example.taskdemo.model.Task
import com.example.taskdemo.model.entities.DaemonTaskEntity
import com.example.taskdemo.model.entities.TaskLockEntity
import com.example.taskdemo.repository.DaemonTaskRepository
import com.example.taskdemo.repository.TaskContextRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
open class DaemonTaskService(
    private val daemonTaskRepository: DaemonTaskRepository,
    private val daemonTaskMapper: DaemonTaskMapper,
    private val taskContextRepository: TaskContextRepository
) {

    @Transactional
    open fun createIfNotExists(task: Task, scheduleLock: TaskLockEntity): Long {
        val daemonTaskEntity = daemonTaskMapper.toEntity(task, scheduleLock);
        daemonTaskEntity.taskContext = taskContextRepository.save(daemonTaskEntity.taskContext)
        daemonTaskRepository.insertIfNotExists(daemonTaskEntity)
        return daemonTaskRepository.findByClazzPath(task.javaClass.name).id ?: -1
    }

    open fun isEnableByClazzPath(clazzPath: String) = daemonTaskRepository.isEnableByClazzPath(clazzPath)

    open fun findAll(): List<DaemonTaskEntity> {
        return daemonTaskRepository.findAll()
    }
}