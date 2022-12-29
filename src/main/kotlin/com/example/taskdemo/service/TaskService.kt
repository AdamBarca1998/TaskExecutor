package com.example.taskdemo.service

import com.example.taskdemo.model.Task
import com.example.taskdemo.model.TaskConfig
import com.example.taskdemo.taskgroup.BasicTaskGroup
import com.example.taskdemo.taskgroup.LinkedTaskGroup
import com.example.taskdemo.taskgroup.PriorityTaskGroup
import org.springframework.stereotype.Service


@Service
class TaskService {

    private val basicTaskGroup = BasicTaskGroup()
    private val priorityTaskGroup = PriorityTaskGroup()
    private val linkedTaskGroup = LinkedTaskGroup()

    fun queue(task: Task) {
        basicTaskGroup.addAndRun(task)
    }

    fun schedule(task: Task, config: TaskConfig) {
        basicTaskGroup.scheduleRun()
    }

    fun runDaemon(task: Task) {

    }
}
