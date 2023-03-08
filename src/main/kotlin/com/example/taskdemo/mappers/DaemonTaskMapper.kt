package com.example.taskdemo.mappers

import com.example.taskdemo.model.Task
import com.example.taskdemo.model.entities.DaemonTaskEntity
import com.example.taskdemo.model.entities.TaskLockEntity
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper(componentModel = "spring")
interface DaemonTaskMapper {

    @Mapping(source = "taskLockEntity", target = "taskLockEntity")
    @Mapping(target = "clazzPath", expression = "java(task.getClass().getName())")
    @Mapping(target = "id", ignore = true)
    fun toEntity(task: Task, taskLockEntity: TaskLockEntity): DaemonTaskEntity
}