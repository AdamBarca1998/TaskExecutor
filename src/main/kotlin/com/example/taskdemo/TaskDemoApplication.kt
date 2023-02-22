package com.example.taskdemo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class TaskDemoApplication

fun main(args: Array<String>) {
	runApplication<TaskDemoApplication>(*args)
}
