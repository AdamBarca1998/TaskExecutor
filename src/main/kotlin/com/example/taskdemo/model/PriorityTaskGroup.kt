package com.example.taskdemo.model

import com.example.taskdemo.interfaces.TaskGroup
import java.time.ZonedDateTime
import java.util.concurrent.PriorityBlockingQueue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PriorityTaskGroup : TaskGroup {

    private val scope = CoroutineScope(Dispatchers.Default)
    private val priorityQueue = PriorityBlockingQueue<Task>()

    init {
        addAllAndRun(
            listOf(
                Task("Task priority 5", ZonedDateTime.now(), priority = 5),
                Task("Task priority 6", ZonedDateTime.now(), priority = 6),
                Task("Task priority 3", ZonedDateTime.now(), priority = 3),
                Task("Task priority -5", ZonedDateTime.now(), priority = -5),
                Task("Task priority 2", ZonedDateTime.now(), priority = 2),
                Task("Task priority 1", ZonedDateTime.now(), priority = 1),
            )
        )
    }

    init {
        scope.launch(Dispatchers.IO) {
            while (true) {
                priorityQueue.take().run()

                if (priorityQueue.isEmpty()) {
                    delay(10_000)
                }
            }
        }
    }

    override fun addAllAndRun(tasks: List<Task>) {
        priorityQueue.addAll(tasks)
    }
}