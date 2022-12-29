package com.example.taskdemo.taskgroup

import com.example.taskdemo.model.Task
import com.example.taskdemo.model.TaskExecutor
import java.util.concurrent.ArrayBlockingQueue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

abstract class TaskGroupAbstract {

    protected val scope = CoroutineScope(Dispatchers.Default)
    protected val plannedTasks = ArrayBlockingQueue<TaskExecutor>(1000, true);

    fun addTask(task: Task) {
        plannedTasks.add(task as TaskExecutor)
    }

    abstract fun run()
}
