package com.example.taskdemo.mappers

import com.example.taskdemo.model.Task
import com.example.taskdemo.model.entities.TaskEntity
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper(componentModel = "spring")
interface TaskMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "taskLockEntity", ignore = true)
    @Mapping(target = "clazz", expression = "java(task.getClass().getName())")
    fun toEntity(task: Task): TaskEntity
}