package com.example.taskdemo.service

import com.example.taskdemo.enums.TaskState
import com.example.taskdemo.model.entities.TaskLockEntity
import com.example.taskdemo.repository.TaskLockRepository
import com.example.taskdemo.taskgroup.CLUSTER_NAME
import com.example.taskdemo.taskgroup.EXPIRED_LOCK_TIME_M
import org.springframework.stereotype.Service

@Service
class TaskLockService(
    private val taskLockRepository: TaskLockRepository
) {

    fun createIfNotExists(name: String): TaskLockEntity {
        val lock = TaskLockEntity()
        lock.name = name

        taskLockRepository.insertIfNotExists(lock)

        return taskLockRepository.findByNameAndClusterName(name, CLUSTER_NAME)
    }

    fun findByName(name: String): TaskLockEntity = taskLockRepository.findByNameAndClusterName(name, CLUSTER_NAME)

    fun tryRefreshLockByName(name: String, appId: String): Boolean {
        return taskLockRepository.tryRefreshLockByNameAndClusterName(name, EXPIRED_LOCK_TIME_M, appId, CLUSTER_NAME) > 0
    }

    fun lockOldestExpiredQueueTask(minutes: Int, appId: String, withoutStates: List<TaskState>): Boolean {
        return taskLockRepository.lockOldestExpiredQueueTaskByClusterName(minutes, appId, withoutStates, CLUSTER_NAME) > 0
    }
}