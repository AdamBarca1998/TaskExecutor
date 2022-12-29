package com.example.taskdemo.taskgroup

import com.example.taskdemo.model.Task
import com.example.taskdemo.model.TaskExecutor
import java.util.concurrent.ArrayBlockingQueue

abstract class TaskGroupAbstract {

    protected val plannedTasks = ArrayBlockingQueue<TaskExecutor>(1000, true);

    fun addAndRun(task: Task) {
        plannedTasks.add(task as TaskExecutor)
        run()
    }

    abstract fun run()
}
