package com.example.taskdemo.taskgroup

import com.example.taskdemo.model.TaskExecutor
import java.time.ZonedDateTime

class FlowTaskGroup : TaskGroupAbstract() {

    init {
        addTask(TaskExecutor("Task linked 1", ZonedDateTime.now()))
        addTask(TaskExecutor("Task linked 2", ZonedDateTime.now()))
        addTask(TaskExecutor("Task linked 3", ZonedDateTime.now()))
        addTask(TaskExecutor("Task linked 4", ZonedDateTime.now()))
        addTask(TaskExecutor("Task linked 5", ZonedDateTime.now()))
        addTask(TaskExecutor("Task linked 6", ZonedDateTime.now()))
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
