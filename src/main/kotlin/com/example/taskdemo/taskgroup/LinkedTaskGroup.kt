package com.example.taskdemo.taskgroup

import com.example.taskdemo.interfaces.TaskGroup
import com.example.taskdemo.model.Task
import java.time.ZonedDateTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class LinkedTaskGroup : TaskGroup {

    private val scope = CoroutineScope(Dispatchers.Default)

    init {
        addAllAndRun(
            listOf(
                Task("Task linked 1", ZonedDateTime.now()),
                Task("Task linked 2", ZonedDateTime.now()),
                Task("Task linked 3", ZonedDateTime.now()),
                Task("Task linked 4", ZonedDateTime.now()),
                Task("Task linked 5", ZonedDateTime.now()),
                Task("Task linked 6", ZonedDateTime.now()),
                Task("Task linked 7", ZonedDateTime.now())
            )
        )
    }

    override fun addAllAndRun(tasks: List<Task>) {
        val flow = flow { tasks.forEach { emit(it) } }

        scope.launch {
            flow.collect { it.run() }
        }
    }
}