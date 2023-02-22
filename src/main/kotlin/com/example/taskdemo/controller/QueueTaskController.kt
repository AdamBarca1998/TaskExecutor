package com.example.taskdemo.controller

import com.example.taskdemo.model.dto.EmailExampleDTO
import com.example.taskdemo.service.QueueTaskService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/queue-tasks")
class QueueTaskController(
    private val queueTaskService: QueueTaskService
) {

    @PostMapping("/email-example")
    fun createEmailExample(@RequestBody emailExampleDTO: EmailExampleDTO): Boolean {
        println(emailExampleDTO.receiver)

        return true
    }
}