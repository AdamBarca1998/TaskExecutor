package com.example.taskdemo.taskgroup

import com.example.taskdemo.model.Task
import com.example.taskdemo.model.TaskConfig
import com.example.taskdemo.model.TaskContext
import java.time.Duration
import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.concurrent.PriorityBlockingQueue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val PLANNER_DELAY_M = 1L

class DaemonTaskGroup : TaskGroup() {

    override val name: String = "DaemonTaskGroup"
    private val savedDaemons = PriorityBlockingQueue<TaskWithConfigAndContext>()

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

        // planner
        scope.launch(Dispatchers.IO) {
            while (true) {
                if (!isLocked.get()) {
                    savedDaemons.forEach { savedDaemon ->
                        val isFoundPlanned = plannedTasks.find { it.task == savedDaemon.task } != null
                        val isFoundRunning = runningTasks.find { it.taskWithConfigAndContext.task == savedDaemon.task } != null

                        if (!isFoundPlanned && !isFoundRunning) {
                            planNextExecution(
                                savedDaemon,
                                Instant.ofEpochMilli(Long.MAX_VALUE).atZone(ZoneOffset.UTC),
                                Instant.ofEpochMilli(Long.MAX_VALUE).atZone(ZoneOffset.UTC)
                            )
                        }
                    }
                }

                delay(Duration.ofMinutes(PLANNER_DELAY_M).toMillis())
            }
        }
    }

    override fun addTask(task: Task, taskConfig: TaskConfig) {
        super.addTask(task, taskConfig)
        savedDaemons.add(TaskWithConfigAndContext(
            task,
            taskConfig,
            TaskContext(
                ZonedDateTime.now(),
                Instant.ofEpochMilli(Long.MAX_VALUE).atZone(ZoneOffset.UTC),
                Instant.ofEpochMilli(Long.MAX_VALUE).atZone(ZoneOffset.UTC)
            )
        ))
    }

    override fun removeTask(task: Task) {
        super.removeTask(task)
        savedDaemons.removeIf { it.task == task }
    }

    override fun planNextExecution(taskWithConfigAndContext: TaskWithConfigAndContext,
                                    lastExecution: ZonedDateTime,
                                    lastCompletion: ZonedDateTime
    ) {
        taskWithConfigAndContext.task.nextExecution()?.let {
            val newContext = TaskContext(it, lastExecution, lastCompletion)

            plannedTasks.add(
                TaskWithConfigAndContext(
                    taskWithConfigAndContext.task,
                    taskWithConfigAndContext.taskConfig,
                    newContext
                )
            )
        }
    }
}