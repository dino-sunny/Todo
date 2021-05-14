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

    val todo = database.getAllTodo()
    val completedTodo = database.getCompletedTodo()

    fun deleteTodo(todo: Todo){
        viewModelScope.launch {
            insert(todo)
        }
    }
    private suspend fun insert(todo: Todo) {
        withContext(Dispatchers.IO) {
            database.delete(todo)
        }
    }

    fun updateTodo(todo: Todo){
        viewModelScope.launch {
            update(todo)
        }
    }
    private suspend fun update(todo: Todo) {
        withContext(Dispatchers.IO) {
            database.update(todo)
        }
    }

    fun onAddTodoClick(){
        _eventAddTodo.value = true
    }
    fun onAddTodoClickComplete(){
        _eventAddTodo.value = false
    }

}