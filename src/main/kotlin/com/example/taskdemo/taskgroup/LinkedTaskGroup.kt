package com.example.taskdemo.taskgroup

import com.example.taskdemo.interfaces.TaskGroup
import com.example.taskdemo.model.TaskImpl
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
                TaskImpl("Task linked 1", ZonedDateTime.now()),
                TaskImpl("Task linked 2", ZonedDateTime.now()),
                TaskImpl("Task linked 3", ZonedDateTime.now()),
                TaskImpl("Task linked 4", ZonedDateTime.now()),
                TaskImpl("Task linked 5", ZonedDateTime.now()),
                TaskImpl("Task linked 6", ZonedDateTime.now()),
                TaskImpl("Task linked 7", ZonedDateTime.now())
            )
        )
    }

    override fun addAllAndRun(tasks: List<TaskImpl>) {
        val flow = flow { tasks.forEach { emit(it) } }

        scope.launch {
            flow.collect { it.run() }
        }
    }
}