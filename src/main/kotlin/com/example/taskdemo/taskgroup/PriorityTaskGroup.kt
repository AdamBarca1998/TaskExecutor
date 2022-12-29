package com.example.taskdemo.taskgroup

import com.example.taskdemo.model.TaskContext
import com.example.taskdemo.model.TaskExecutor
import java.util.concurrent.PriorityBlockingQueue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PriorityTaskGroup : TaskGroupAbstract() {

    private val priorityQueue = PriorityBlockingQueue<TaskExecutor>()

    init {
        scope.launch(Dispatchers.IO) {
            while (true) {
                shiftTasks()

                while (priorityQueue.isNotEmpty()) {
                    priorityQueue.poll()?.run(TaskContext())

                    shiftTasks()
                }

                sleepLaunch()
            }
        }
    }

    private fun shiftTasks() {
        priorityQueue.addAll(plannedTasks)
        plannedTasks.clear()
    }
}
