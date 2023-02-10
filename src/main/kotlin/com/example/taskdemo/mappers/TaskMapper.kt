package com.example.taskdemo.mappers

import com.example.taskdemo.model.Task
import com.example.taskdemo.model.entities.TaskEntity
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Named

@Mapper(componentModel = "spring")
interface TaskMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "taskLockEntity", ignore = true)
    @Mapping(source = "task", target = "clazz", qualifiedByName = ["taskToClazz"])
    fun toEntity(task: Task): TaskEntity

    @Named("taskToClazz")
    fun taskToClazz(task: Task): String {
        return task::class.java.name //TODO: FIXME
    }
}