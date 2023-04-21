package com.example.taskdemo.tasks.schedules

import com.example.taskdemo.annotations.Schedule
import com.example.taskdemo.annotations.TaskSchedule
import com.example.taskdemo.model.Task
import com.example.taskdemo.model.entities.TaskContext
import java.time.Duration
import java.time.LocalDateTime

@TaskSchedule(
    schedules = [
        Schedule(second = "0")
    ],
    maxWaitDuration = "PT30S"
)
class ScheduleTask1mExample : Task {

    override var id = -1L

    override fun run(taskContext: TaskContext) {
        println("${LocalDateTime.now()}  ScheduleTask1mExample running...")
        Thread.sleep(Duration.ofSeconds(5))
    }
}