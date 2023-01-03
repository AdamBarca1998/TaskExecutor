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
        scope.launch(Dispatchers.IO) {
            while (true) {
                plannedTasks.poll()?.run(TaskContext())

                sleepLaunch()
            }
        }
    }

    override fun stop() {
        TODO("Not yet implemented")
    }
}
