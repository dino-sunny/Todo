package com.dino.todo.database

import androidx.room.*

@Dao
interface TodoDatabaseDao {
    @Insert
    fun insert(todo: Todo)

    @Update
    fun update(todo: Todo)

    @Delete
    fun delete(todo: Todo)

    @Query("SELECT * FROM todo_table")
    fun getAllTodo(): Todo?
}