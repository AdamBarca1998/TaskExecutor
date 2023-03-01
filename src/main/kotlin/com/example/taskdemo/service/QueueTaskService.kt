package com.example.taskdemo.service

import com.example.taskdemo.enums.QueueTaskState
import com.example.taskdemo.mappers.QueueTaskMapper
import com.example.taskdemo.model.Task
import com.example.taskdemo.repository.QueueTaskRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
open class QueueTaskService(
    private val queueTaskRepository: QueueTaskRepository,
    private val taskLockService: TaskLockService,
    private val queueTaskMapper: QueueTaskMapper
) {

    open fun saveTask(task: Task) {
        queueTaskRepository.save(queueTaskMapper.toEntity(task))
    }

    @Transactional
    open fun findExpired(minutes: Int, appId: String): List<Task> {
        val expiredEntities = queueTaskRepository.findExpired(minutes)
        val lockedEntities = expiredEntities.stream()
            .filter { taskLockService.tryRefreshLockByName(it.taskLockEntity.name, minutes, appId) }
            .toList()

        return lockedEntities.stream()
            .map { queueTaskMapper.toTask(it) }
            .toList()
    }

    open fun updateState(task: Task, state: QueueTaskState): Boolean {
        return queueTaskRepository.updateStateById(task.id, state) > 0
    }

    open fun updateStateAndResult(task: Task, state: QueueTaskState, result: String): Boolean {
        return queueTaskRepository.updateStateAndResultById(task.id, state, result) > 0
    }
}