package com.example.taskdemo.taskgroup

import com.example.taskdemo.model.Task
import com.example.taskdemo.model.TaskConfig
import com.example.taskdemo.model.TaskContext
import java.util.concurrent.LinkedTransferQueue
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import mu.KotlinLogging

abstract class TaskGroup {

    abstract val name: String
    protected val scope = CoroutineScope(Dispatchers.Default)
    protected val plannedTasks = LinkedTransferQueue<TaskWithConfigAndContext>()
    protected var isLocked: AtomicBoolean = AtomicBoolean(false)
    protected val logger = KotlinLogging.logger {}

    fun addTask(task: Task, taskContext: TaskContext? = null, taskConfig: TaskConfig? = null) {
        plannedTasks.add(TaskWithConfigAndContext(task, taskContext, taskConfig))
    }

    open fun removeTask(task: Task) {
        plannedTasks.removeIf { it.task == task }
    }

    abstract fun start()

    fun stop() {
        isLocked.set(true)
    }

    protected suspend fun sleepLaunch() {
        if (plannedTasks.isEmpty()) {
            delay(10_000)
        }
    }

    data class TaskWithConfigAndContext(val task: Task, val taskContext: TaskContext?, val taskConfig: TaskConfig?)
}
