package com.example.taskdemo.tasks.schedules

import com.example.taskdemo.annotations.Schedule
import com.example.taskdemo.annotations.ScheduleTask
import com.example.taskdemo.model.Task
import com.example.taskdemo.model.entities.TaskContext
import java.time.LocalDateTime

@ScheduleTask(
    schedules = [
        Schedule(second = "0")
    ],
)
class ScheduleTask1mExample : Task {

    override var id = -1L

    override fun run(taskContext: TaskContext) {
        println("${LocalDateTime.now()}  ScheduleTask1mExample running...")
    }
}