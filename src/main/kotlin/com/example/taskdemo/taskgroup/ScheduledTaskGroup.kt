package com.example.taskdemo.taskgroup

import com.example.taskdemo.extensions.toNullable
import com.example.taskdemo.model.Task
import com.example.taskdemo.model.TaskScheduleContext
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.LinkedTransferQueue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ScheduledTaskGroup : TaskGroup() {

    override val name: String = "ScheduledTaskGroup"
    private val runningTasks = LinkedTransferQueue<TaskWithJob>()

    init {
        start()
    }

    override fun start() {
//        if (isLocked) {
            isLocked = false
//
//            runningTasks.forEach {
//                scope.launch {
//                    runTask(it)
//                }
//            }
//        }

        scope.launch(Dispatchers.IO) {
            while (!isLocked) {
                while (plannedTasks.isNotEmpty()) {
                    launch {
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

    override fun removeTask(task: Task) {
        super.removeTask(task)
        val foundTask = runningTasks.stream().filter{it.taskWithConfigAndContext.task == task}.findFirst().toNullable()
        foundTask?.job?.cancel()
    }

    private suspend fun runTask(taskWithConfigAndContext: TaskWithConfigAndContext) {
        // start
        delay(
            ChronoUnit.MILLIS.between(
                ZonedDateTime.now(),
                taskWithConfigAndContext.taskContext?.taskScheduleContext?.startDateTime
            )
        )

        do {
            taskWithConfigAndContext.task.run(taskWithConfigAndContext.taskContext)
            // period
            delay(
                ChronoUnit.MILLIS.between(
                    ZonedDateTime.now(),
                    taskWithConfigAndContext.taskConfig?.nextExecution(
                        TaskScheduleContext(
                            ZonedDateTime.now(),
                            ZonedDateTime.now(),
                            ZonedDateTime.now()
                        )
                    )
                )
            )
        } while (taskWithConfigAndContext.taskConfig?.taskSchedules?.isNotEmpty() == true && !isLocked)
    }

    data class TaskWithJob(val taskWithConfigAndContext: TaskWithConfigAndContext, val job: Job)
}
