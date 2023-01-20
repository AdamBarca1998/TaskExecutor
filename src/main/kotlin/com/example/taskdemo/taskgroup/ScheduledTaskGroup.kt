package com.example.taskdemo.taskgroup

import com.example.taskdemo.extensions.toNullable
import com.example.taskdemo.model.Task
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.Executors
import java.util.concurrent.LinkedTransferQueue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ScheduledTaskGroup : TaskGroup() {

    override val name: String = "ScheduledTaskGroup"
    private val runningTasks = LinkedTransferQueue<TaskWithJob>()
    private val runningHeavyTasks = LinkedTransferQueue<TaskWithJob>()
    private val singleThreadDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()

    init {
        scope.launch(Dispatchers.IO) {
            while (true) {
                if (!isLocked.get()) {
                    if (runningTasks.isEmpty() || plannedTasks.firstOrNull()?.taskConfig?.isHeavy == true) {
                        runTask()
                    }
                }

                sleepLaunch()
            }
        }
    }

    override fun removeTask(task: Task) {
        super.removeTask(task)
        runningTasks.stream().filter { it.taskWithConfigAndContext.task == task }.findFirst().toNullable()?.let {
            runningTasks.remove(it)
            it.job.cancel()
        }
        runningHeavyTasks.stream().filter { it.taskWithConfigAndContext.task == task }.findFirst().toNullable()?.let {
            runningHeavyTasks.remove(it)
            it.job.cancel()
        }
    }

    private suspend fun runTask() {
        if (!isLocked.get()) {
            plannedTasks.poll()?.let { taskWithConfigAndContext ->
                val job = scope.launch(
                    if (taskWithConfigAndContext.taskConfig.isHeavy) {
                        singleThreadDispatcher
                    } else {
                        Dispatchers.IO
                    }
                ) {
                    val scheduleContext = taskWithConfigAndContext.taskContext.taskScheduleContext

                    // start
                    delay(ChronoUnit.MILLIS.between(ZonedDateTime.now(), scheduleContext.startDateTime))

                    scheduleContext.lastExecution = ZonedDateTime.now()
                    logger.debug { "${taskWithConfigAndContext.task} started." }

                    try {
                        taskWithConfigAndContext.task.run(taskWithConfigAndContext.taskContext)

                        scheduleContext.lastCompletion = ZonedDateTime.now()
                        logger.debug { "${taskWithConfigAndContext.task} ended." }
                    } catch (e: Exception) {
                        logger.error { "${taskWithConfigAndContext.task} $e" }
                    }

                    taskWithConfigAndContext.taskConfig.nextExecution(taskWithConfigAndContext.taskContext.taskScheduleContext)
                        ?.let {
                            taskWithConfigAndContext.taskContext.taskScheduleContext.startDateTime = it
                            plannedTasks.add(taskWithConfigAndContext)
                        }

                    runningTasks.removeIf { it.taskWithConfigAndContext == taskWithConfigAndContext }
                    runningHeavyTasks.removeIf { it.taskWithConfigAndContext == taskWithConfigAndContext }
                }

                if (taskWithConfigAndContext.taskConfig.isHeavy) {
                    runningHeavyTasks.add(TaskWithJob(taskWithConfigAndContext, job))
                } else {
                    runningTasks.add(TaskWithJob(taskWithConfigAndContext, job))
                }
            }
        }
    }

    data class TaskWithJob(val taskWithConfigAndContext: TaskWithConfigAndContext, val job: Job)
}
