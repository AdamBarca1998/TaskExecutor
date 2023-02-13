package com.example.taskdemo.service

import com.example.taskdemo.repository.TaskLockRepository
import org.springframework.stereotype.Service

@Service
class TaskLockService(
    private val taskLockRepository: TaskLockRepository
) {

    fun findByName(name: String) = taskLockRepository.findByName(name)

    fun findExpiredLocks(minutes: Int) = taskLockRepository.findExpiredLocks(minutes)

    fun refreshLockById(id: Long, minutes: Int, appId: String) = taskLockRepository.refreshLockById(id, minutes, appId) == 1

    fun findById(id: Long) = taskLockRepository.findById(id)
}