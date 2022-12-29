package com.example.taskdemo.taskgroup

import com.example.taskdemo.model.TaskExecutor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.time.ZonedDateTime

class LinkedTaskGroup : TaskGroupAbstract() {

    private val scope = CoroutineScope(Dispatchers.Default)

    init {
        addAndRun(TaskExecutor("Task linked 1", ZonedDateTime.now()))
        addAndRun(TaskExecutor("Task linked 2", ZonedDateTime.now()))
        addAndRun(TaskExecutor("Task linked 3", ZonedDateTime.now()))
        addAndRun(TaskExecutor("Task linked 4", ZonedDateTime.now()))
        addAndRun(TaskExecutor("Task linked 5", ZonedDateTime.now()))
        addAndRun(TaskExecutor("Task linked 6", ZonedDateTime.now()))
    }

//    override fun addAllAndRun(tasks: List<TaskExecutor>) {
//        val flow = flow { tasks.forEach { emit(it) } }
//
//        scope.launch {
//            flow.collect { it.run(TaskContext()) }
//        }
//    }

    override fun run() {

    }
}
