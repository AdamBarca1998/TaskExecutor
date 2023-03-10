package com.example.taskdemo.mappers

import com.example.taskdemo.model.TaskContext
import com.example.taskdemo.model.entities.TaskContextEntity
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper(componentModel = "spring")
interface TaskContextMapper {

    @Mapping(target = "id", ignore = true)
    fun toEntity(taskContext: TaskContext): TaskContextEntity
}