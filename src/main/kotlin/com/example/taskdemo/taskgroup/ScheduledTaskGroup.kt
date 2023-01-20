package com.example.taskdemo.taskgroup

import java.util.concurrent.Executors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch

class ScheduledTaskGroup : TaskGroup() {

    override val name: String = "ScheduledTaskGroup"
    private val heavyThreadDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    private val normalThreadDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()

    init {
        scope.launch(Dispatchers.IO) {
            while (true) {
                if (!isLocked.get()) {
                    while (plannedTasks.isNotEmpty()) {
                        plannedTasks.poll()?.let {
                            val job = launch(
                                if (it.taskConfig.isHeavy) {
                                    heavyThreadDispatcher
                                } else {
                                    normalThreadDispatcher
                                }
                            ) { runTask(it) }

                            runningTasks.add(TaskWithJob(it, job))
                        }
                    }
                }

                sleepLaunch()
            }
        }
    }
}
