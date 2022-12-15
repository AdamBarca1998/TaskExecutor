package com.example.taskdemo.extensions

import java.util.*

// https://gist.github.com/bastman/87faa0e410d08f1650c319a331418665
fun <T : Any> Optional<T>.toNullable(): T? {
    return if (this.isPresent) {
        this.get()
    } else {
        null
    }
}
