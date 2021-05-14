package com.dino.todo.home

import android.app.Application
import androidx.lifecycle.*
import com.dino.todo.database.Todo
import com.dino.todo.database.TodoDatabaseDao
import com.dino.todo.utility.formatNights
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddTodoViewModel (
    val database: TodoDatabaseDao,
    application: Application
) : AndroidViewModel(application) {

    private val _eventSubmitTodo = MutableLiveData<Boolean>()
    val eventSubmit: LiveData<Boolean> get() = _eventSubmitTodo

    private val _eventCancel = MutableLiveData<Boolean>()
    val eventCancel: LiveData<Boolean> get() = _eventCancel

    private val _eventDate = MutableLiveData<Boolean>()
    val eventDate: LiveData<Boolean> get() = _eventDate

    private val _eventTime = MutableLiveData<Boolean>()
    val eventTime: LiveData<Boolean> get() = _eventTime

    fun addTodo(todo: Todo){
        viewModelScope.launch {
            insert(todo)
        }
    }
    private suspend fun insert(night: Todo) {
        withContext(Dispatchers.IO) {
            database.insert(night)
        }
    }

    fun onSubmitClick(){
        _eventSubmitTodo.value = true
    }
    fun onSubmitComplete(){
        _eventSubmitTodo.value = false
    }

    fun onCancelClick(){
        _eventCancel.value = true
    }
    fun onCancelComplete(){
        _eventCancel.value = false
    }

    fun onDateClick(){
        _eventDate.value = true
    }
    fun onDateClickComplete(){
        _eventDate.value = false
    }

    fun onTimeClick(){
        _eventTime.value = true
    }
    fun onTimeClickComplete(){
        _eventTime.value = false
    }

}