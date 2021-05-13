package com.dino.todo.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todo_table")
data class Todo(
        @PrimaryKey(autoGenerate = true)
        var todoId: Long = 0L,
        val title: String = "",
        var description: String = "",
        var date: Long = System.currentTimeMillis(),
        var completed: Boolean = false
)