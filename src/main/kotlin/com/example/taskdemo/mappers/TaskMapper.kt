package com.example.taskdemo.mappers

import com.example.taskdemo.model.Task
import com.example.taskdemo.model.TaskConfig
import com.example.taskdemo.model.entities.TaskEntity
import com.example.taskdemo.model.entities.TaskLockEntity
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper(componentModel = "spring")
interface TaskMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "taskLockEntity", target = "taskLockEntity")
    @Mapping(target = "clazz", expression = "java(task.getClass().getName())")
    fun toEntity(task: Task, config: TaskConfig, taskLockEntity: TaskLockEntity?): TaskEntity
}