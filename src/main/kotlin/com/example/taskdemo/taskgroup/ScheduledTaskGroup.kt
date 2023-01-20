package com.example.taskdemo.taskgroup

import com.example.taskdemo.model.Task
import java.time.Duration
import java.util.concurrent.Executors
import java.util.concurrent.LinkedTransferQueue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ScheduledTaskGroup : TaskGroup() {

    override val name: String = "ScheduledTaskGroup"
    private val runningHeavyTasks = LinkedTransferQueue<TaskWithJob>()
    private val singleThreadDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()

    init {
        scope.launch(Dispatchers.IO) {
            while (true) {
                runTask()

                sleepLaunch()

                if (runningTasks.isNotEmpty()) {
                    delay(Duration.ofSeconds(10).toMillis())
                }
            }
        }
    }

    override fun removeTask(task: Task) {
        super.removeTask(task)
        runningHeavyTasks.find { it.taskWithConfigAndContext.task == task }?.let {
            runningHeavyTasks.remove(it)
            it.job.cancel()
        }
    }

    private suspend fun runTask() {
        if (!isLocked.get() && (runningTasks.isEmpty() || plannedTasks.firstOrNull()?.taskConfig?.isHeavy == true) ) {
            plannedTasks.poll()?.let { taskWithConfigAndContext ->
                val job = scope.launch(
                    if (taskWithConfigAndContext.taskConfig.isHeavy) {
                        singleThreadDispatcher
                    } else {
                        Dispatchers.IO
                    }
                ) {
                    startTask(taskWithConfigAndContext)

                    taskWithConfigAndContext.taskConfig.nextExecution(taskWithConfigAndContext.taskContext.taskScheduleContext)
                        ?.let {
                            taskWithConfigAndContext.taskContext.taskScheduleContext.startDateTime = it
                            plannedTasks.add(taskWithConfigAndContext)
                        }

                    runningTasks.removeIf { it.taskWithConfigAndContext == taskWithConfigAndContext }
                    runningHeavyTasks.removeIf { it.taskWithConfigAndContext == taskWithConfigAndContext }

                    runTask()
                }

                if (taskWithConfigAndContext.taskConfig.isHeavy) {
                    runningHeavyTasks.add(TaskWithJob(taskWithConfigAndContext, job))
                } else {
                    runningTasks.add(TaskWithJob(taskWithConfigAndContext, job))
                }
            }
        }
    }
}
