package com.example.taskdemo.taskgroup

import com.example.taskdemo.model.Task
import com.example.taskdemo.model.TaskContext
import com.example.taskdemo.model.TaskExecutor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.util.concurrent.PriorityBlockingQueue

class PriorityTaskGroup : TaskGroupAbstract() {

    private val scope = CoroutineScope(Dispatchers.Default)
    private val priorityQueue = PriorityBlockingQueue<Task>()

    init {
        addAndRun(TaskExecutor("Task priority 5", ZonedDateTime.now(), priority = 5))
        addAndRun(TaskExecutor("Task priority 6", ZonedDateTime.now(), priority = 6))
        addAndRun(TaskExecutor("Task priority 3", ZonedDateTime.now(), priority = 3))
        addAndRun(TaskExecutor("Task priority -5", ZonedDateTime.now(), priority = -5))
        addAndRun(TaskExecutor("Task priority 2", ZonedDateTime.now(), priority = 2))
        addAndRun(TaskExecutor("Task priority 1", ZonedDateTime.now()))
    }

    init {
        scope.launch(Dispatchers.IO) {
            while (true) {
                priorityQueue.take().run(TaskContext())

                if (priorityQueue.isEmpty()) {
                    delay(10_000)
                }
            }
        }
    }

    override fun run() {
        priorityQueue.add(plannedTasks.take())
    }
}
