package com.example.taskdemo.taskgroup

import com.example.taskdemo.model.TaskContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class QueueTaskGroup : TaskGroupAbstract() {

    init {
        scope.launch(Dispatchers.IO) {
            while (true) {
                plannedTasks.poll()?.run(TaskContext())

                sleepLaunch()
            }
        }
    }
}
