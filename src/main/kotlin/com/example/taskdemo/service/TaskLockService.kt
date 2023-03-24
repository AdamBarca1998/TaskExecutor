package com.example.taskdemo.service

import com.example.taskdemo.enums.QueueTaskState
import com.example.taskdemo.model.entities.TaskLockEntity
import com.example.taskdemo.repository.TaskLockRepository
import com.example.taskdemo.taskgroup.EXPIRED_LOCK_TIME_M
import org.springframework.stereotype.Service

@Service
class TaskLockService(
    private val taskLockRepository: TaskLockRepository
) {

//    fun findExpiredLocks(minutes: Int) = taskLockRepository.findExpiredLocks(minutes)

    fun findByName(name: String): TaskLockEntity = taskLockRepository.findByName(name)

    fun tryRefreshLockByName(name: String, appId: String): Boolean {
        return taskLockRepository.tryRefreshLockByName(name, EXPIRED_LOCK_TIME_M, appId) > 0
    }

    fun lockOldestExpiredQueue(minutes: Int, appId: String, withoutStates: List<QueueTaskState>): Boolean {
        return taskLockRepository.lockOldestExpiredQueue(minutes, appId, withoutStates) > 0
    }
}