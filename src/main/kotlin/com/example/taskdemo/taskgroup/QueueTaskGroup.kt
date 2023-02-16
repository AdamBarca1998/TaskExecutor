package com.example.taskdemo.taskgroup

import com.example.taskdemo.service.TaskService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

open class QueueTaskGroup(
    taskService: TaskService
) : TaskGroup(
    taskService
) {

    override val name: String = "QueueTaskGroup"

    init {
        scope.launch(Dispatchers.IO) {
            while (true) {
                if (!isLocked.get() && runningTasks.isEmpty()) {
                    plannedTasks.poll()?.let {
                        val job = launch { runTask(it) }

                        runningTasks.add(TaskWithJob(it, job))
                    }
                }

                sleepLaunch()
            }
        }
    }
}
