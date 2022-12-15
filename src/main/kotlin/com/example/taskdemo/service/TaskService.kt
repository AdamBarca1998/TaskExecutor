package com.example.taskdemo.service

import com.example.taskdemo.model.Task
import com.example.taskdemo.taskgroup.BasicTaskGroup
import com.example.taskdemo.taskgroup.LinkedTaskGroup
import com.example.taskdemo.taskgroup.PriorityTaskGroup
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
