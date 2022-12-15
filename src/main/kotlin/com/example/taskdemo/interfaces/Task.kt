package com.example.taskdemo.interfaces

import java.time.ZonedDateTime

interface Task<T> : Comparable<T> {

    fun run()

    fun getNextTime(): ZonedDateTime?
}