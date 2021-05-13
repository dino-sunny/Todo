package com.dino.todo.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.dino.todo.database.Todo
import com.dino.todo.database.TodoDatabaseDao
import com.dino.todo.utility.formatNights
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel (
    val database: TodoDatabaseDao,
    application: Application
) : AndroidViewModel(application) {

    private val todo = database.getAllTodo()
    val todos = Transformations.map(todo){todos->
        formatNights(todos)
    }

    fun addTodo(){
        viewModelScope.launch {
            val todo = Todo()
            insert(todo)
        }
    }
    private suspend fun insert(night: Todo) {
        withContext(Dispatchers.IO) {
            database.insert(night)
        }
    }
}