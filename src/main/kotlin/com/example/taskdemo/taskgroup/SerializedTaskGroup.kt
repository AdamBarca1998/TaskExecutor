package com.example.taskdemo.taskgroup

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

abstract class SerializedTaskGroup : TaskGroup() {

    init {
        scope.launch(Dispatchers.IO) {
            while (true) {
                try {
                    if (!isLocked.get() && runningTasks.isEmpty()) {
                        plannedTasks.poll()?.let {
                            val job = launch { runTask(it) }

                            runningTasks.add(TaskWithJob(it, job))
                        }
                    }
                } catch (e: Exception) {
                    logger.error { e }
                } finally {
                    sleepLaunch()
                }
            }
        }
    }
}
