package com.dino.todo.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TodoDatabaseDao {
    @Insert
    fun insert(todo: Todo)

    @Update
    fun update(todo: Todo)

    @Delete
    fun delete(todo: Todo)

    @Query("SELECT * FROM todo_table WHERE completed = 0 ORDER BY todoId DESC")
    fun getAllTodo(): LiveData<List<Todo>>

    @Query("SELECT * FROM todo_table WHERE completed = 1 ORDER BY todoId DESC")
    fun getCompletedTodo(): LiveData<List<Todo>>
}