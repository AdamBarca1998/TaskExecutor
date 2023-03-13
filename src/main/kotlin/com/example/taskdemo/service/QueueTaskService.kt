package com.example.taskdemo.service

import com.example.taskdemo.dto.QueueTaskDto
import com.example.taskdemo.enums.QueueTaskState
import com.example.taskdemo.mappers.QueueTaskMapper
import com.example.taskdemo.model.Task
import com.example.taskdemo.repository.QueueTaskRepository
import com.example.taskdemo.taskgroup.EXPIRED_LOCK_TIME_M
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
open class QueueTaskService(
    private val queueTaskRepository: QueueTaskRepository,
    private val taskLockService: TaskLockService,
    private val queueTaskMapper: QueueTaskMapper
) {

    private val finishedStates = listOf(
        QueueTaskState.FINISHED,
        QueueTaskState.CANCELED
    )

    open fun saveTask(task: Task) {
        queueTaskRepository.save(queueTaskMapper.toEntity(task))
    }

    @Transactional
    open fun findExpired(appId: String): List<Task> {
        val expiredEntities = queueTaskRepository.findExpired(EXPIRED_LOCK_TIME_M, finishedStates)
        val lockedEntities = expiredEntities.stream()
            .filter { taskLockService.tryRefreshLockByName(it.taskLockEntity.name, appId) }
            .toList()

        return lockedEntities.stream()
            .map { queueTaskMapper.toTask(it) }
            .toList()
    }

    open fun refreshLocks(ids: List<Long>): Boolean {
        return queueTaskRepository.refreshTasksByIds(ids, EXPIRED_LOCK_TIME_M) == ids.size
    }

    open fun updateStateById(id: Long, state: QueueTaskState): Boolean {
        return queueTaskRepository.updateStateById(id, state, finishedStates) > 0
    }

    open fun updateStateAndResultById(id: Long, state: QueueTaskState, result: String): Boolean {
        return queueTaskRepository.updateStateAndResultById(id, state, result) > 0
    }

    open fun findAll(): List<QueueTaskDto> {
        val queueTaskEntities = queueTaskRepository.findAllByStateNotIn(finishedStates)

        return queueTaskEntities.stream()
            .map { queueTaskMapper.toDto(it) }
            .toList()
    }
}