package com.example.taskdemo.controller

import com.example.taskdemo.model.dto.EmailExampleDTO
import com.example.taskdemo.service.TaskService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/queue-tasks")
class QueueTaskController(
    private val taskService: TaskService
) {

    @PostMapping("/email-example")
    fun createEmailExample(@RequestBody emailExampleDTO: EmailExampleDTO): Boolean {
        println(emailExampleDTO.receiver)

        return true
    }
}