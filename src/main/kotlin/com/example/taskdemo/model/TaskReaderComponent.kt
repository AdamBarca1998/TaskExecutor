package com.example.taskdemo.model

import com.example.taskdemo.abstractschedule.AbstractSchedule
import com.example.taskdemo.annotations.DEFAULT_FIXED_RATE_OR_DELAY
import com.example.taskdemo.annotations.DaemonTask
import com.example.taskdemo.annotations.Schedule
import com.example.taskdemo.annotations.ScheduleTask
import com.example.taskdemo.service.TaskGroupService
import java.time.Duration
import java.time.ZonedDateTime
import org.reflections.Reflections
import org.reflections.scanners.Scanners
import org.reflections.util.ClasspathHelper
import org.reflections.util.ConfigurationBuilder
import org.springframework.scheduling.support.CronExpression
import org.springframework.stereotype.Component

private const val TASKS_PATH = "com.example.taskdemo.tasks"
private const val SCHEDULES_PATH = ".schedules."

@Component
class TaskReaderComponent(
    private val taskGroupService: TaskGroupService,
) {

    private val reflections = Reflections(
        ConfigurationBuilder()
            .addUrls(ClasspathHelper.forPackage(TASKS_PATH))
    )
    private val scheduleTasks: List<Task> = reflections[Scanners.TypesAnnotated.with(ScheduleTask::class.java).asClass<Any>()].stream()
        .map {
            when(it.name) {
                TASKS_PATH + SCHEDULES_PATH + "EraserUselessTasksST" -> {
                    Class.forName(it.name).getDeclaredConstructor(TaskGroupService::class.java).newInstance(taskGroupService)
                }
                else -> Class.forName(it.name).getDeclaredConstructor().newInstance()
            }
        }
        .filter { it is Task }
        .map { it as Task }
        .toList()
    private val daemonTasks: List<Task> = reflections[Scanners.TypesAnnotated.with(DaemonTask::class.java).asClass<Any>()].stream()
        .map { Class.forName(it.name).getDeclaredConstructor().newInstance() }
        .filter { it is Task }
        .map { it as Task }
        .toList()

    init {
        scheduleTasks.forEach { task ->
            val annotations = task::class.java.annotations
            val taskConfig = TaskConfig.Builder()

            annotations.forEach { annotation ->
                if (annotation is ScheduleTask) {
                    annotation.schedules.forEach { schedule ->
                        taskConfig.addSchedule(convertAnnotationToSchedule(schedule))
                    }
                    taskConfig.withPriority(annotation.priority)
                    taskConfig.withType(annotation.type)
                    taskConfig.withStartDateTime(
                        if (annotation.startDateTime == "NOW")
                            ZonedDateTime.now()
                        else
                            ZonedDateTime.parse(annotation.startDateTime)
                    )
                    taskConfig.withMaxWaitDuration(Duration.parse(annotation.maxWaitDuration))
                }
            }

            taskGroupService.addSchedule(task, taskConfig.build())
        }
        daemonTasks.forEach { taskGroupService.addDaemon(it) }
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