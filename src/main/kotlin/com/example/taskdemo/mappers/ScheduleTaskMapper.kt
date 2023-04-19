package com.example.taskdemo.mappers

import com.example.taskdemo.model.Task
import com.example.taskdemo.model.entities.ScheduleTaskEntity
import com.example.taskdemo.model.entities.TaskContext
import com.example.taskdemo.model.entities.TaskLockEntity
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper(componentModel = "spring")
interface ScheduleTaskMapper {

    @Mapping(source = "taskLockEntity", target = "taskLockEntity")
    @Mapping(target = "clazzPath", expression = "java(task.getClass().getName())")
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "taskContext", target = "taskContext")
    fun toEntity(task: Task, taskLockEntity: TaskLockEntity, taskContext: TaskContext): ScheduleTaskEntity
}