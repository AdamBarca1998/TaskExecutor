package com.example.taskdemo.service

import com.example.taskdemo.model.dto.EmailTaskExampleDTO
import org.springframework.stereotype.Service

@Service
class QueueTaskService {

    fun createTask(queueTaskDTO: EmailTaskExampleDTO) {
        println(queueTaskDTO)
    }
}