package com.example.taskdemo.executor

import com.example.taskdemo.model.Task
import kotlinx.coroutines.*
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.PriorityBlockingQueue

class TaskExecutor {

    private val scope = CoroutineScope(Dispatchers.Default)
    private val priorityQueue = PriorityBlockingQueue<Task>()

    init {
        scope.launch(Dispatchers.IO) {
            while (true) {
                startTask(priorityQueue.take())
                yield()
            }
        }
    }

    fun runTasks(tasks: List<Task>) {
        scope.launch {
            launch {
                val priorityTasks = tasks.filter { it.priority != null }
                priorityQueue.addAll(priorityTasks)
            }

            launch {
                val classicTasks = tasks.filter { it.priority == null }
                classicTasks.forEach {
                    launch {
                        startTask(it)
                    }
                }
            }
        }
    }

    private fun startTask(task: Task) {
        scope.launch {
            delay(ChronoUnit.MILLIS.between(ZonedDateTime.now(), task.startTime)) // start

            do {
                task.doIt()
                delay(ChronoUnit.MILLIS.between(ZonedDateTime.now(), task.getNextTime() ?: ZonedDateTime.now())) // period
            } while (task.cron != null)
        }
    }
}