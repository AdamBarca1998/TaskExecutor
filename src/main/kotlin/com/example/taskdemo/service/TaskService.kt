package com.example.taskdemo.service

import com.example.taskdemo.model.TaskImpl
import com.example.taskdemo.taskgroup.BasicTaskGroup
import com.example.taskdemo.taskgroup.LinkedTaskGroup
import com.example.taskdemo.taskgroup.PriorityTaskGroup
import org.springframework.stereotype.Service


@Service
class TaskService {

    private val basicTaskGroup = BasicTaskGroup()
    private val priorityTaskGroup = PriorityTaskGroup()
    private val linkedTaskGroup = LinkedTaskGroup()

    fun addToBasicGroup(tasks: List<TaskImpl>) {
        basicTaskGroup.addAllAndRun(tasks)
    }

    fun addToPriorityGroup(tasks: List<TaskImpl>) {
        priorityTaskGroup.addAllAndRun(tasks)
    }

    fun addToLinkedGroup(tasks: List<TaskImpl>) {
        linkedTaskGroup.addAllAndRun(tasks)
    }
}
