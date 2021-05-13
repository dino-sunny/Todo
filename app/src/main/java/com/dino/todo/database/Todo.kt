package com.dino.todo.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todo_table")
data class Todo(
        @PrimaryKey(autoGenerate = true)
        var todoId: Long = 0L,
        @ColumnInfo(name = "title")
        var title: String = "",
        @ColumnInfo(name = "description")
        var description: String = "",
        var date: Long = System.currentTimeMillis(),
        var completed: Boolean = false
)