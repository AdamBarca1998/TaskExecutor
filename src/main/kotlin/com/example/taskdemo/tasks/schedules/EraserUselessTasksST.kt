package com.example.taskdemo.tasks.schedules

import com.example.taskdemo.annotations.Schedule
import com.example.taskdemo.annotations.ScheduleTask
import com.example.taskdemo.enums.ScheduleTaskType
import com.example.taskdemo.model.Task
import com.example.taskdemo.model.entities.TaskContext
import com.example.taskdemo.service.TaskGroupService

@ScheduleTask(
    schedules = [
        Schedule(second = "0", minute = "0", hour = "0"),
    ],
    type = ScheduleTaskType.EVERY
)
class EraserUselessTasksST(
    private val taskGroupService: TaskGroupService,
) : Task {

    override var id = -1L

    override fun run(taskContext: TaskContext) {
        taskGroupService.eraserUselessTasks()
    }
}