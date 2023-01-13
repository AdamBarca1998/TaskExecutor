package com.example.taskdemo.service

import com.example.taskdemo.model.Task
import com.example.taskdemo.model.TaskConfig
import com.example.taskdemo.model.TaskContext
import com.example.taskdemo.model.TaskImpl
import com.example.taskdemo.taskgroup.QueueTaskGroup
import com.example.taskdemo.taskgroup.ScheduledTaskGroup
import org.springframework.stereotype.Service


@Service
class TaskService {

    private val scheduledTaskGroup = ScheduledTaskGroup()
    private val queueTaskGroup = QueueTaskGroup()

    init {
//        demoQueue()
    }

    fun runSchedule(task: Task, context: TaskContext, config: TaskConfig) {
        scheduledTaskGroup.addTask(task, context, config)
    }

    fun runQueue(task: Task) {
        queueTaskGroup.addTask(task)
    }

    fun runDaemon(task: Task) {

    }

    fun removeTask(task: Task) {
        scheduledTaskGroup.removeTask(task)
        queueTaskGroup.removeTask(task)
    }

    private fun demoQueue() {
        queueTaskGroup.stop()

        val task4 = TaskImpl("Task linked 4")

        runQueue(TaskImpl("Task linked 1"))
        runQueue(TaskImpl("Task linked 2"))
        runQueue(TaskImpl("Task linked 3"))
        runQueue(task4)
        runQueue(TaskImpl("Task linked 5"))
        runQueue(TaskImpl("Task linked 6"))

        Thread.sleep(5_000)
        removeTask(task4)

        Thread.sleep(10_000)
        queueTaskGroup.start()
    }
}
