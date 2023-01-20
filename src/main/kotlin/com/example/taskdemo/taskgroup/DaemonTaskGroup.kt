package com.example.taskdemo.taskgroup

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DaemonTaskGroup : TaskGroup() {

    override val name: String = "DaemonTaskGroup"

    init {
        scope.launch(Dispatchers.IO) {
            while (true) {
                if (!isLocked.get()) {
                    while (plannedTasks.isNotEmpty()) {
                        plannedTasks.poll()?.let {
                            val job = launch { runTask(it) }

                            runningTasks.add(TaskWithJob(it, job))
                        }
                    }
                }

                sleepLaunch()
            }
        }
    }
}