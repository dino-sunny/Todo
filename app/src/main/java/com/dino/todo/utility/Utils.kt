package com.dino.todo.utility

import com.dino.todo.database.Todo

fun formatNights(nights: List<Todo>) {
    val sb = StringBuilder()
    sb.apply {
        nights.forEach {
            append(it.title)
        }
    }
}

