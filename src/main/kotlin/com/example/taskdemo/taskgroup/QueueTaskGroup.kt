package com.example.taskdemo.taskgroup

import com.example.taskdemo.model.TaskContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class QueueTaskGroup : TaskGroupAbstract() {

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
