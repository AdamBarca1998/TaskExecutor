package com.example.taskdemo.taskgroup

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class QueueTaskGroup : TaskGroup() {

    override val name: String = "QueueTaskGroup"

    init {
        start()
    }

    override fun start() {
        isLocked = false

        scope.launch(Dispatchers.IO) {
            while (!isLocked) {
                plannedTasks.poll()?.let {
                    it.task.run(it.taskContext)
                }

                sleepLaunch()
            }
        }
    }
}
