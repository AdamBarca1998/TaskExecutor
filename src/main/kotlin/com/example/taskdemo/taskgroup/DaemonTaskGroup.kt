package com.example.taskdemo.taskgroup

import com.example.taskdemo.model.DaemonTaskContext
import com.example.taskdemo.service.TaskService
import java.time.ZonedDateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DaemonTaskGroup(
    taskService: TaskService
) : TaskGroup(
    taskService
) {

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

    override fun planNextExecution(taskWithConfig: TaskWithConfig,
                                    lastExecution: ZonedDateTime,
                                    lastCompletion: ZonedDateTime
    ) {
        val context = DaemonTaskContext(taskWithConfig.taskConfig.startDateTime, lastExecution, lastCompletion)

        context.nextExecution().let {
            taskWithConfig.taskConfig.startDateTime = it ?: ZonedDateTime.now().plusDays(1L)
            plannedTasks.add(taskWithConfig)
        }
    }
}