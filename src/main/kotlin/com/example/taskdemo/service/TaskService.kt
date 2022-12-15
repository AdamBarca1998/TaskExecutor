package com.example.taskdemo.service

import com.example.taskdemo.model.BasicTaskGroup
import com.example.taskdemo.model.LinkedTaskGroup
import com.example.taskdemo.model.PriorityTaskGroup
import com.example.taskdemo.model.Task
import org.springframework.stereotype.Service


@Service
class TaskService {

    private val basicTaskGroup = BasicTaskGroup()
    private val priorityTaskGroup = PriorityTaskGroup()
    private val linkedTaskGroup = LinkedTaskGroup()

    fun addToBasicGroup(tasks: List<Task>) {
        basicTaskGroup.addAllAndRun(tasks)
    }

    fun addToPriorityGroup(tasks: List<Task>) {
        priorityTaskGroup.addAllAndRun(tasks)
    }

    fun addToLinkedGroup(tasks: List<Task>) {
        linkedTaskGroup.addAllAndRun(tasks)
    }
}
