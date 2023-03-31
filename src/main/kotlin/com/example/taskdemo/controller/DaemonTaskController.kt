package com.example.taskdemo.controller

import com.example.taskdemo.service.TaskGroupService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/daemon-tasks")
class DaemonTaskController(
    private val taskGroupService: TaskGroupService
) {

    @GetMapping("/get-all")
    fun getAll() = ResponseEntity.ok(taskGroupService.getAllDaemons())

    @PutMapping("/cancel/{id}")
    fun cancel(@PathVariable id: Long) = ResponseEntity.ok(taskGroupService.cancelDaemonById(id))

    @PutMapping("/run/{id}")
    fun runById(@PathVariable id: Long) = ResponseEntity.ok(taskGroupService.runDaemonById(id))
}