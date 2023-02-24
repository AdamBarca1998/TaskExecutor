package com.example.taskdemo.controller

import com.example.taskdemo.service.TaskGroupService
import com.example.taskdemo.tasks.queues.EmailTaskExample
import java.util.*
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/queue-tasks")
class ExampleController(
    private val taskGroupService: TaskGroupService
) {

    @PostMapping("/email-example")
    fun emailExample(@RequestParam("receiver") receiver: String): Boolean {
        taskGroupService.addQueue(EmailTaskExample(receiver))

        return true
    }
}