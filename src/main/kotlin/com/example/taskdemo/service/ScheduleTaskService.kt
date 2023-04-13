package com.example.taskdemo.service

import com.example.taskdemo.mappers.ScheduleTaskMapper
import com.example.taskdemo.model.Task
import com.example.taskdemo.model.entities.ScheduleTaskEntity
import com.example.taskdemo.model.entities.TaskContext
import com.example.taskdemo.model.entities.TaskLockEntity
import com.example.taskdemo.repository.ScheduleTaskRepository
import com.example.taskdemo.taskgroup.CLUSTER_NAME
import org.springframework.stereotype.Service

@Service
class ScheduleTaskService(
    private val scheduleTaskRepository: ScheduleTaskRepository,
    private val scheduleTaskMapper: ScheduleTaskMapper
) {

    fun createIfNotExists(task: Task, scheduleLock: TaskLockEntity): Long {
        scheduleTaskRepository.insertIfNotExists(scheduleTaskMapper.toEntity(task, scheduleLock))
        return scheduleTaskRepository.findByClazzPath(task.javaClass.name).id ?: -1
    }

    fun isEnableById(id: Long) = scheduleTaskRepository.isEnableById(id)

    fun findAll(): List<ScheduleTaskEntity> {
        return scheduleTaskRepository.findAllByClusterName(CLUSTER_NAME)
    }

    fun updateContextById(id: Long, context: TaskContext): Boolean {
        return scheduleTaskRepository.updateContextById(id, context) == 1
    }
}