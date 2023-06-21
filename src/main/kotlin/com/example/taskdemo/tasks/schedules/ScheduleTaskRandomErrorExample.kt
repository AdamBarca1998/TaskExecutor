package com.example.taskdemo.tasks.schedules

import com.example.taskdemo.annotations.Schedule
import com.example.taskdemo.annotations.ScheduleTask
import com.example.taskdemo.model.Task
import com.example.taskdemo.model.entities.TaskContext
import java.time.Duration
import java.time.LocalDateTime

@ScheduleTask(
    schedules = [
        Schedule(second = "0", minute = "0/1")
    ],
)
class ScheduleTaskRandomErrorExample : Task {

    override var id = -1L

    override fun run(taskContext: TaskContext) {
        println("${LocalDateTime.now()}  ScheduleTaskRandomErrorExample running...")
        Thread.sleep(Duration.ofSeconds(30))

        if ((0..1).shuffled().last() == 1) {
            throw NullPointerException("ScheduleTaskErrorExample")
        }
    }
}