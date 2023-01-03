package com.example.taskdemo.taskgroup

import com.example.taskdemo.model.TaskContext
import com.example.taskdemo.model.TaskExecutor
import java.util.concurrent.PriorityBlockingQueue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PriorityTaskGroup : TaskGroupAbstract() {

    private val priorityQueue = PriorityBlockingQueue<TaskExecutor>()
    override val name: String = "PriorityTaskGroup"

    init {
        start()
    }

    private fun shiftTasks() {
        priorityQueue.addAll(plannedTasks)
        plannedTasks.clear()
    }

    override fun start() {
        scope.launch(Dispatchers.IO) {
            while (true) {
                shiftTasks()

                while (priorityQueue.isNotEmpty()) {
                    priorityQueue.poll()?.run(TaskContext(null))

                    shiftTasks()
                }

                sleepLaunch()
            }
        }
    }

    override fun stop() {
        TODO("Not yet implemented")
    }
}
