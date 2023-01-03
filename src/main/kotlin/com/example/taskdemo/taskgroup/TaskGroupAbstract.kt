package com.example.taskdemo.taskgroup

import com.example.taskdemo.model.Task
import com.example.taskdemo.model.TaskExecutor
import java.util.concurrent.LinkedTransferQueue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay

abstract class TaskGroupAbstract {

    abstract val name: String
    protected val scope = CoroutineScope(Dispatchers.Default)
    protected val plannedTasks = LinkedTransferQueue<TaskExecutor>();

    fun addTask(task: Task) {
        plannedTasks.add(task as TaskExecutor)
    }

    fun removeTask(task: Task) {
        // TODO:IMPL
    }

    abstract fun start()

    abstract fun stop()

    protected suspend fun sleepLaunch() {
        if (plannedTasks.isEmpty()) {
            delay(10_000)
        }
    }
}
