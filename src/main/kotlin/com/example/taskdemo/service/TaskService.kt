package com.example.taskdemo.service

import com.example.taskdemo.model.Task
import com.example.taskdemo.model.TaskConfig
import com.example.taskdemo.model.TaskExecutor
import com.example.taskdemo.taskgroup.PriorityTaskGroup
import com.example.taskdemo.taskgroup.QueueTaskGroup
import com.example.taskdemo.taskgroup.ScheduledTaskGroup
import java.time.ZonedDateTime
import kotlin.random.Random
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.springframework.stereotype.Service


@Service
class TaskService {

    private val scheduledTaskGroup = ScheduledTaskGroup()
    private val priorityTaskGroup = PriorityTaskGroup()
    private val queueTaskGroup = QueueTaskGroup()

    protected val scope = CoroutineScope(Dispatchers.Default)
    init {
//        // basic
//        val cronEvery7s = CronBuilder.cron(CronDefinitionBuilder.instanceDefinitionFor(CronType.SPRING))
//            .withSecond(FieldExpressionFactory.every(7))
//            .instance()
//        val cronEvery5s = CronBuilder.cron(CronDefinitionBuilder.instanceDefinitionFor(CronType.SPRING))
//            .withSecond(FieldExpressionFactory.every(5))
//            .instance()
//
//        runSchedule(TaskExecutor("Task 10 seconds", ZonedDateTime.now().plusSeconds(10)), TaskConfig())
//        runSchedule(TaskExecutor("Task 20 seconds Cron: 5s, 7s", ZonedDateTime.now().plusSeconds(20), cronList = listOf(cronEvery5s, cronEvery7s)), TaskConfig())
//        runSchedule(TaskExecutor("Task 30 seconds", ZonedDateTime.now().plusSeconds(30)), TaskConfig())
//        //
//
//        // queue
//        runQueue(TaskExecutor("Task linked 1", ZonedDateTime.now()))
//        runQueue(TaskExecutor("Task linked 2", ZonedDateTime.now()))
//        runQueue(TaskExecutor("Task linked 3", ZonedDateTime.now()))
//        runQueue(TaskExecutor("Task linked 4", ZonedDateTime.now()))
//        runQueue(TaskExecutor("Task linked 5", ZonedDateTime.now()))
//        runQueue(TaskExecutor("Task linked 6", ZonedDateTime.now()))

        // priority
        demo(TaskExecutor("Task priority 5", ZonedDateTime.now(), priority = 5))
        demo(TaskExecutor("Task priority 6", ZonedDateTime.now(), priority = 6))
        demo(TaskExecutor("Task priority 3", ZonedDateTime.now(), priority = 3))
        demo(TaskExecutor("Task priority -5", ZonedDateTime.now(), priority = -5))
        demo(TaskExecutor("Task priority 2", ZonedDateTime.now(), priority = 2))
        demo(TaskExecutor("Task priority null", ZonedDateTime.now()))
        demo(TaskExecutor("Task priority 9", ZonedDateTime.now(), priority = 9))
    }

    fun runScheduled(task: Task, config: TaskConfig) {
        scheduledTaskGroup.addTask(task)
    }

    fun runQueue(task: Task) {
        queueTaskGroup.addTask(task)
    }

    fun runPriority(task: Task) {
        priorityTaskGroup.addTask(task)
    }

    fun runDaemon(task: Task) {

    }

    private fun demo(task: Task) {
        scope.launch {
            delay(Random.nextLong(1000, 10000))
            println("add Task $task")
            runPriority(task)
        }
    }
}
