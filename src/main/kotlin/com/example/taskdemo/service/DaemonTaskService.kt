package com.example.taskdemo.service

import com.example.taskdemo.mappers.DaemonTaskMapper
import com.example.taskdemo.model.Task
import com.example.taskdemo.model.entities.DaemonTaskEntity
import com.example.taskdemo.model.entities.TaskContext
import com.example.taskdemo.model.entities.TaskLockEntity
import com.example.taskdemo.repository.DaemonTaskRepository
import com.example.taskdemo.taskgroup.CLUSTER_NAME
import org.springframework.stereotype.Service

@Service
class DaemonTaskService(
    private val daemonTaskRepository: DaemonTaskRepository,
    private val daemonTaskMapper: DaemonTaskMapper
) {

    fun createIfNotExists(task: Task, scheduleLock: TaskLockEntity, taskContext: TaskContext): Long {
        daemonTaskRepository.insertIfNotExists(daemonTaskMapper.toEntity(task, scheduleLock, taskContext))
        return daemonTaskRepository.findByClazzPath(task.javaClass.name).id ?: -1
    }

    fun isEnableById(id: Long) = daemonTaskRepository.isEnableById(id)

    fun findAll(): List<DaemonTaskEntity> {
        return daemonTaskRepository.findAllByClusterName(CLUSTER_NAME)
    }

    fun updateContextById(id: Long, context: TaskContext): Boolean {
        return daemonTaskRepository.updateContextById(id, context) == 1
    }

    fun eraserUselessTasksByClazzPathNotIn(clazzPaths: List<String>): Int {
        return daemonTaskRepository.eraserUselessTasksByClazzPathNotIn(clazzPaths)
    }
}