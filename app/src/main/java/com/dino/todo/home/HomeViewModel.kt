package com.dino.todo.home

import android.app.Application
import androidx.lifecycle.*
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

    private val _eventAddTodo = MutableLiveData<Boolean>()
    val eventTodo: LiveData<Boolean> get() = _eventAddTodo

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

    fun onAddTodoClick(){
        _eventAddTodo.value = true
    }
    fun onAddTodoClickComplete(){
        _eventAddTodo.value = false
    }

}