package com.example.taskdemo.tasks.schedules

import com.example.taskdemo.model.Schedule
import com.example.taskdemo.model.Task
import com.example.taskdemo.model.TaskContext
import com.example.taskdemo.model.TaskSchedule
import java.time.Duration
import java.time.LocalDateTime

@TaskSchedule(
    schedules = [
        Schedule(second = "*/15")
    ],
    startDateTime = "2023-02-09T16:22:00Z",
    heavy = true
)
class HeavyScheduleTaskExample : Task {

    override fun run(taskContext: TaskContext) {
        println("${LocalDateTime.now()}  HeavyScheduleTaskExample running...")
        Thread.sleep(Duration.ofSeconds(1))
    }
}