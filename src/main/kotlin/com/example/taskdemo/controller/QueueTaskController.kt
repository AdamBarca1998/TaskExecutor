package com.example.taskdemo.controller

import com.example.taskdemo.service.QueueTaskService
import com.example.taskdemo.service.TaskGroupService
import com.example.taskdemo.tasks.queues.EmailTaskExample
import java.util.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/queue-tasks")
class QueueTaskController(
    private val taskGroupService: TaskGroupService,
    private val queueTaskService: QueueTaskService
) {

    @PostMapping("/email-example")
    fun emailExample(@RequestParam("receiver") receiver: String): Boolean {
        taskGroupService.addQueue(EmailTaskExample(receiver))

        return true
    }

    @GetMapping("/get-all")
    fun getAll() = ResponseEntity.ok(queueTaskService.findAll())

    @PutMapping("/cancel/{id}")
    fun cancel(@PathVariable id: Long) = ResponseEntity.ok(taskGroupService.cancelQueueTaskById(id))

    @GetMapping("/start")
    fun start() = ResponseEntity.ok(taskGroupService.start())
}