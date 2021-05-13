package com.dino.todo.home

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.dino.todo.R
import com.dino.todo.database.Todo
import com.dino.todo.database.TodoDatabase
import com.dino.todo.databinding.AddTodoFragmentBinding

class AddTodoFragment : DialogFragment() {
    private lateinit var addTodoViewModel: AddTodoViewModel
    private lateinit var binding: AddTodoFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.add_todo_fragment, container, false)
        val application = requireNotNull(this.activity).application
        val dataSource = TodoDatabase.getInstance(application).todoDatabaseDao
        val viewModelFactory = AddTodoViewModelFactory(dataSource, application)
        addTodoViewModel = ViewModelProvider(this,viewModelFactory).get(AddTodoViewModel::class.java)
        binding.addTodoViewModel = addTodoViewModel

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        setObservers()
    }

    private fun setObservers() {
        addTodoViewModel.eventSubmit.observe(viewLifecycleOwner, { clicked ->
            if (clicked) {
                val newTodo = Todo()
                newTodo.title = binding.titleEditText.text.toString()
                newTodo.description = binding.descriptionEditText.text.toString()
                addTodoViewModel.addTodo(newTodo)
                addTodoViewModel.onSubmitComplete()
                dismiss()
            }
        })
    }


}