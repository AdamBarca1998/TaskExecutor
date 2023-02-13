package com.example.taskdemo.taskgroup

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

open class QueueTaskGroup : TaskGroup() {

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
