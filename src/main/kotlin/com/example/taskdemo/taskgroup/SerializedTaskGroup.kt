package com.example.taskdemo.taskgroup

import com.example.taskdemo.service.TaskService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

abstract class SerializedTaskGroup(
    taskService: TaskService
) : TaskGroup(
    taskService
) {

    override val name: String = "SerializedTaskGroup"

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
