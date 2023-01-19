package com.example.taskdemo.taskgroup

import com.example.taskdemo.model.Task
import com.example.taskdemo.model.TaskConfig
import com.example.taskdemo.model.TaskContext
import com.example.taskdemo.model.TaskScheduleContext
import java.time.ZonedDateTime
import java.util.concurrent.LinkedTransferQueue
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import mu.KotlinLogging

abstract class TaskGroup {

    abstract val name: String
    protected val scope = CoroutineScope(Dispatchers.Default)
    protected val plannedTasks = LinkedTransferQueue<TaskWithConfigAndContext>()
    protected var isLocked: AtomicBoolean = AtomicBoolean(false)
    protected val logger = KotlinLogging.logger {}

    fun addTask(task: Task,
                taskContext: TaskContext = TaskContext(TaskScheduleContext(ZonedDateTime.now(), ZonedDateTime.now(), ZonedDateTime.now())),
                taskConfig: TaskConfig = TaskConfig.Builder().build()) {

        plannedTasks.add(TaskWithConfigAndContext(task, taskContext, taskConfig))
    }

    open fun removeTask(task: Task) {
        plannedTasks.removeIf { it.task == task }
    }

    fun start() {
        isLocked.set(false)
    }

    fun stop() {
        isLocked.set(true)
    }

    protected suspend fun sleepLaunch() {
        if (plannedTasks.isEmpty() || isLocked.get()) {
            delay(10_000)
        }
    }

    data class TaskWithConfigAndContext(val task: Task, val taskContext: TaskContext, val taskConfig: TaskConfig): Comparable<TaskWithConfigAndContext> {

        override fun compareTo(other: TaskWithConfigAndContext): Int {
            val compareTime = taskContext.taskScheduleContext.startDateTime.compareTo(other.taskContext.taskScheduleContext.startDateTime)

            return if (compareTime == 0) {
                val comparePriority = other.taskConfig.priority.compareTo(taskConfig.priority)

                return if (comparePriority == 0) {
                    return if (taskConfig.isHeavy) {
                        -1
                    } else {
                        1
                    }
                } else {
                    comparePriority
                }
            } else {
                compareTime
            }
        }
    }
}
