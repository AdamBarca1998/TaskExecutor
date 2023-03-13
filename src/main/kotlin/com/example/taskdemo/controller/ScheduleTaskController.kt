package com.example.taskdemo.controller

import com.example.taskdemo.service.TaskGroupService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/schedule-tasks")
class ScheduleTaskController(
    private val taskGroupService: TaskGroupService
) {

    @GetMapping("/get-all")
    fun getAll() = ResponseEntity.ok(taskGroupService.getAllSchedules())

    @PutMapping("/cancel/{clazzPath}")
    fun cancel(@PathVariable clazzPath: String) = ResponseEntity.ok(taskGroupService.cancelScheduleByClazzPath(clazzPath))
}