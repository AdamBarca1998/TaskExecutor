package com.example.taskdemo.tasks

import com.example.taskdemo.TaskType
import com.example.taskdemo.model.Task
import com.example.taskdemo.service.TaskService
import org.reflections.Reflections
import org.reflections.scanners.Scanners
import org.springframework.stereotype.Component

@Component
class TaskReaderComponent {

    private val taskService = TaskService()
    private val reflections = Reflections("com.example.taskdemo.tasks")
    private val tasks: List<Task> = reflections[Scanners.TypesAnnotated.with(TaskAnnotation::class.java).asClass<Any>()].stream()
        .map { Class.forName(it.name).getDeclaredConstructor().newInstance() }
        .filter { it is Task }
        .map { it as Task }
        .toList()

    init {
        tasks.forEach {
            when (it.getConfig().type) {
                TaskType.QUEUE -> taskService.addQueue(it)
                TaskType.SCHEDULED -> taskService.addSchedule(it, it.getConfig())
                TaskType.DAEMON -> taskService.addDaemon(it)
            }
        }
    }
}