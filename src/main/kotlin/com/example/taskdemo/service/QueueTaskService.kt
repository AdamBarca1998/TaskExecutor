package com.example.taskdemo.service

import com.example.taskdemo.dto.QueueTaskDto
import com.example.taskdemo.enums.TaskState
import com.example.taskdemo.mappers.QueueTaskMapper
import com.example.taskdemo.model.Task
import com.example.taskdemo.repository.QueueTaskRepository
import com.example.taskdemo.taskgroup.CLUSTER_NAME
import com.example.taskdemo.taskgroup.EXPIRED_LOCK_TIME_M
import org.springframework.stereotype.Service

@Service
class QueueTaskService(
    private val queueTaskRepository: QueueTaskRepository,
    private val taskLockService: TaskLockService,
    private val queueTaskMapper: QueueTaskMapper
) {

    private val finishedStates = listOf(
        TaskState.FINISHED,
        TaskState.CANCELED
    )

    fun saveTask(task: Task) {
        queueTaskRepository.save(queueTaskMapper.toEntity(task))
    }

    fun findOldestExpired(appId: String): Task? {
        return if (taskLockService.lockOldestExpiredQueueTask(EXPIRED_LOCK_TIME_M, appId, finishedStates)) {
            queueTaskMapper.toTask(queueTaskRepository.getNewestLocked(appId, CLUSTER_NAME))
        } else {
            null
        }
    }

    fun refreshLockByTaskId(id: Long): Boolean {
        return queueTaskRepository.refreshLockByTaskId(id, EXPIRED_LOCK_TIME_M) > 0
    }

    fun updateStateById(id: Long, state: TaskState): Boolean {
        return queueTaskRepository.updateStateById(id, state, finishedStates) > 0
    }

    fun findById(id: Long) = queueTaskMapper.toTask(queueTaskRepository.findById(id).orElse(null))

    fun findAll(): List<QueueTaskDto> {
        val queueTaskEntities = queueTaskRepository.findAllByStateNotInAndClusterName(finishedStates, CLUSTER_NAME)

        return queueTaskEntities.stream()
            .map { queueTaskMapper.toDto(it) }
            .toList()
    }
}