package com.example.taskdemo.model

import com.example.taskdemo.abstractschedule.AbstractSchedule
import com.example.taskdemo.service.TaskGroupService
import java.time.Duration
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import org.reflections.Reflections
import org.reflections.scanners.Scanners
import org.springframework.scheduling.support.CronExpression
import org.springframework.stereotype.Component

private const val TASKS_PATH = "com.example.taskdemo.tasks"

@Component
class TaskReaderComponent(val taskGroupService: TaskGroupService) {

    private val dateTimeFormatter = DateTimeFormatter.ISO_INSTANT.withZone(ZoneOffset.UTC)
    private val reflections = Reflections(TASKS_PATH)
    private val scheduleTasks: List<Task> = reflections[Scanners.TypesAnnotated.with(TaskSchedule::class.java).asClass<Any>()].stream()
        .map { Class.forName(it.name).getDeclaredConstructor().newInstance() }
        .filter { it is Task }
        .map { it as Task }
        .toList()

    init {
        scheduleTasks.forEach { task ->
            val annotations = task::class.java.annotations
            val taskConfig = TaskConfig.Builder()

            annotations.forEach { annotation ->
                if (annotation is TaskSchedule) {
                    annotation.schedules.forEach { schedule ->
                        taskConfig.addSchedule(convertAnnotationToSchedule(schedule))
                    }
                    taskConfig.withPriority(annotation.priority)
                    taskConfig.withHeavy(annotation.heavy)
                    taskConfig.withStartDateTime(ZonedDateTime.parse(annotation.startDateTime, dateTimeFormatter))
                }
            }

            taskGroupService.addSchedule(task, taskConfig.build())
        }
    }

    private fun convertAnnotationToSchedule(annotation: Schedule): AbstractSchedule {
        return if (annotation.fixedDelay != DEFAULT_FIXED_RATE_OR_DELAY) {
            AbstractSchedule.fromFixedDelay(Duration.of(annotation.fixedDelay, annotation.timeUnit.toChronoUnit()))
        } else if (annotation.fixedRate != DEFAULT_FIXED_RATE_OR_DELAY) {
            AbstractSchedule.fromFixedRate(Duration.of(annotation.fixedRate, annotation.timeUnit.toChronoUnit()))
        } else {
            AbstractSchedule.fromCron(
                CronExpression.parse(
                    "${annotation.second} ${annotation.minute} ${annotation.hour} " +
                            "${annotation.dayOfMonth} ${annotation.month} ${annotation.dayOfWeek}"
                )
            )
        }
    }
}