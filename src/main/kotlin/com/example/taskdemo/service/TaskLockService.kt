package com.example.taskdemo.service

import com.example.taskdemo.model.entities.TaskLockEntity
import com.example.taskdemo.repository.TaskLockRepository
import org.springframework.stereotype.Service

@Service
class TaskLockService(
    private val taskLockRepository: TaskLockRepository
) {

//    fun findExpiredLocks(minutes: Int) = taskLockRepository.findExpiredLocks(minutes)

    fun findByName(name: String): TaskLockEntity = taskLockRepository.findByName(name)

    fun tryRefreshLockByName(name: String, minutes: Int, appId: String): Boolean {
        return taskLockRepository.tryRefreshLockByName(name, minutes, appId) > 0
    }
}