package com.example.taskdemo.tasks.schedules

import com.example.taskdemo.annotations.Schedule
import com.example.taskdemo.annotations.ScheduleTask
import com.example.taskdemo.model.Task
import com.example.taskdemo.model.entities.TaskContext
import java.time.Duration
import java.time.LocalDateTime

@ScheduleTask(
    schedules = [
        Schedule(second = "0", minute = "*/1")
    ]
)
class ScheduleTaskRandomTimeExample : Task {

    override var id = -1L

    override fun run(taskContext: TaskContext) {
        val randomTime = (0L..30).shuffled().last()

        println("${LocalDateTime.now()}  ScheduleTaskRandomTimeExample $randomTime")
        Thread.sleep(Duration.ofSeconds(randomTime))
    }
}