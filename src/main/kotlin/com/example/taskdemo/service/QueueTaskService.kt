package com.example.taskdemo.service

import com.example.taskdemo.mappers.QueueTaskMapper
import com.example.taskdemo.model.Task
import com.example.taskdemo.repository.ScheduleTaskRepository
import org.springframework.stereotype.Service

@Service
class QueueTaskService(
    private val scheduleTaskRepository: ScheduleTaskRepository,
    private val queueTaskMapper: QueueTaskMapper
) {

    fun createTask(task: Task) {
        val a = queueTaskMapper.toEntity(task)

        println(a)
    }
}