package com.dino.todo.home

import android.app.Application
import androidx.lifecycle.*
import com.dino.todo.database.Todo
import com.dino.todo.database.TodoDatabaseDao
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

    //Function to delete a task
    fun deleteTodo(todo: Todo){
        viewModelScope.launch {
            delete(todo)
        }
    }
    //Deleting in a IO thread
    private suspend fun delete(todo: Todo) {
        withContext(Dispatchers.IO) {
            database.delete(todo)
        }
    }

    //Function to update a task
    fun updateTodo(todo: Todo){
        viewModelScope.launch {
            update(todo)
        }
    }
    //Updating DB in IO thread using coroutine
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