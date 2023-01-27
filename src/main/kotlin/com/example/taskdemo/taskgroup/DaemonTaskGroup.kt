package com.example.taskdemo.taskgroup

import com.example.taskdemo.model.DaemonTaskContext
import com.example.taskdemo.model.Task
import com.example.taskdemo.model.TaskConfig
import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.concurrent.LinkedTransferQueue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DaemonTaskGroup : TaskGroup() {

    override val name: String = "DaemonTaskGroup"
    private val savedDaemons = LinkedTransferQueue<TaskWithConfig>()

    init {
        scope.launch(Dispatchers.IO) {
            while (true) {
                if (!isLocked.get()) {
                    // planner
                    savedDaemons.forEach { savedDaemon ->
                        if (plannedTasks.find { it.task == savedDaemon.task } == null &&
                            runningTasks.find { it.taskWithConfig.task == savedDaemon.task } == null) {

                            planNextExecution(
                                savedDaemon,
                                Instant.ofEpochMilli(Long.MAX_VALUE).atZone(ZoneOffset.UTC),
                                Instant.ofEpochMilli(Long.MAX_VALUE).atZone(ZoneOffset.UTC)
                            )
                        }
                    }

                    // runner
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

    override fun addTask(task: Task, taskConfig: TaskConfig) {
        super.addTask(task, taskConfig)
        savedDaemons.add(TaskWithConfig(task, taskConfig))
    }

    override fun removeTask(task: Task) {
        super.removeTask(task)
        savedDaemons.removeIf { it.task == task }
    }

    override fun planNextExecution(taskWithConfig: TaskWithConfig,
                                    lastExecution: ZonedDateTime,
                                    lastCompletion: ZonedDateTime
    ) {
        val context = DaemonTaskContext(taskWithConfig.taskConfig.startDateTime, lastExecution, lastCompletion)

        context.nextExecution()?.let {
            taskWithConfig.taskConfig.startDateTime = it
            plannedTasks.add(taskWithConfig)
        }
    }
}