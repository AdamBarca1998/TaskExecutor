package com.example.taskdemo.taskgroup

import com.example.taskdemo.model.DaemonTaskContext
import com.example.taskdemo.model.Task
import com.example.taskdemo.model.TaskConfig
import java.time.ZonedDateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DaemonTaskGroup : TaskGroup() {

    init {
        scope.launch(Dispatchers.IO) {
            while (true) {
                try {
                    if (!isLocked.get()) {
                        while (plannedTasks.isNotEmpty()) {
                            plannedTasks.poll()?.let {
                                val job = launch { runTask(it) }

                                runningTasks.add(TaskWithJob(it, job))
                            }
                        }
                    }
                } catch (e: Exception) {
                    logger.error { e }
                } finally {
                    delay(getNextRefreshMillis())
                }
            }
        }
    }

    override fun addTask(task: Task, taskConfig: TaskConfig) {
    }

    override fun isEnable(task: Task): Boolean = true

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