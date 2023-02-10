package com.example.taskdemo.tasks

import com.example.taskdemo.model.Task
import com.example.taskdemo.model.TaskConfig
import com.example.taskdemo.service.TaskService
import com.example.taskdemo.taskschedule.Schedule
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import org.reflections.Reflections
import org.reflections.scanners.Scanners
import org.springframework.scheduling.support.CronExpression
import org.springframework.stereotype.Component

@Component
class TaskReaderComponent {

    private val taskService = TaskService()
    private val dateTimeFormatter = DateTimeFormatter.ISO_INSTANT.withZone(ZoneOffset.UTC)

    private val reflections = Reflections("com.example.taskdemo.tasks")

    private val scheduleTasks: List<Task> = reflections[Scanners.TypesAnnotated.with(Schedule::class.java).asClass<Any>()].stream()
        .map { Class.forName(it.name).getDeclaredConstructor().newInstance() }
        .filter { it is Task }
        .map { it as Task }
        .toList()

    init {
        scheduleTasks.forEach { task ->
            val annotations = task::class.java.annotations
            val taskConfig = TaskConfig.Builder()

            annotations.forEach { annotation ->
                if (annotation is Schedule) {
                    taskConfig.addSchedule(Schedule.fromCron(convertAnnotationToCron(annotation)))
                    taskConfig.withPriority(annotation.priority)
                    taskConfig.withHeavy(annotation.heavy)
                    taskConfig.withStartDateTime(ZonedDateTime.parse(annotation.startDateTime, dateTimeFormatter))
                }
            }

            taskService.addSchedule(task, taskConfig.build())
        }
    }

    private fun convertAnnotationToCron(annotation: Schedule): CronExpression {
        return CronExpression.parse(
            "${annotation.second} ${annotation.minute} ${annotation.hour} " +
                    "${annotation.dayOfMonth} ${annotation.month} ${annotation.dayOfWeek}"
        )
    }
}