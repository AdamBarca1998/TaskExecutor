package com.example.taskdemo.taskgroup

import com.example.taskdemo.interfaces.TaskGroup
import com.example.taskdemo.model.TaskImpl
import java.time.ZonedDateTime
import java.util.concurrent.PriorityBlockingQueue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PriorityTaskGroup : TaskGroup {

    private val scope = CoroutineScope(Dispatchers.Default)
    private val priorityQueue = PriorityBlockingQueue<TaskImpl>()

    init {
        addAllAndRun(
            listOf(
                TaskImpl("Task priority 5", ZonedDateTime.now(), priority = 5),
                TaskImpl("Task priority 6", ZonedDateTime.now(), priority = 6),
                TaskImpl("Task priority 3", ZonedDateTime.now(), priority = 3),
                TaskImpl("Task priority -5", ZonedDateTime.now(), priority = -5),
                TaskImpl("Task priority 2", ZonedDateTime.now(), priority = 2),
                TaskImpl("Task priority 1", ZonedDateTime.now(), priority = 1),
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

    override fun addAllAndRun(tasks: List<TaskImpl>) {
        priorityQueue.addAll(tasks)
    }
}