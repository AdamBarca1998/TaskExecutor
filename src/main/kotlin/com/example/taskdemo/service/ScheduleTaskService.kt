package com.example.taskdemo.service

import com.example.taskdemo.mappers.ScheduleTaskMapper
import com.example.taskdemo.model.Task
import com.example.taskdemo.model.entities.ScheduleTaskEntity
import com.example.taskdemo.model.entities.TaskLockEntity
import com.example.taskdemo.repository.ScheduleTaskRepository
import com.example.taskdemo.repository.TaskContextRepository
import com.example.taskdemo.taskgroup.CLUSTER_NAME
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
open class ScheduleTaskService(
    private val scheduleTaskRepository: ScheduleTaskRepository,
    private val scheduleTaskMapper: ScheduleTaskMapper,
    private val taskContextRepository: TaskContextRepository
) {

    @Transactional
    open fun createIfNotExists(task: Task, scheduleLock: TaskLockEntity): Long {
        val scheduleTaskEntity = scheduleTaskMapper.toEntity(task, scheduleLock)
        scheduleTaskEntity.taskContext = taskContextRepository.save(scheduleTaskEntity.taskContext)
        scheduleTaskRepository.insertIfNotExists(scheduleTaskEntity)
        return scheduleTaskRepository.findByClazzPath(task.javaClass.name).id ?: -1
    }

    open fun isEnableById(id: Long) = scheduleTaskRepository.isEnableById(id)

    open fun findAll(): List<ScheduleTaskEntity> {
        return scheduleTaskRepository.findAllByClusterName(CLUSTER_NAME)
    }
}