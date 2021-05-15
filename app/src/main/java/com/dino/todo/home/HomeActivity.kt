package com.dino.todo.home

import android.app.Dialog
import android.os.Bundle
import android.view.Window
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.dino.todo.R
import com.dino.todo.database.Todo
import com.dino.todo.database.TodoDatabase
import com.dino.todo.databinding.ActivityHomeBinding
import com.dino.todo.utility.Constants.ConstantVariables.TODO_UPDATE
import com.google.gson.Gson

class HomeActivity : AppCompatActivity(),OnTodoClickListener {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var todoAdapter: TodoAdapter
    private lateinit var completedTodoAdapter: TodoAdapter
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        val dataSource = TodoDatabase.getInstance(this.application).todoDatabaseDao
        val viewModelFactory = HomeViewModelFactory(dataSource, application)
        homeViewModel =
            ViewModelProvider(
                    this, viewModelFactory).get(HomeViewModel::class.java)
        binding.lifecycleOwner = this
        binding.homeViewModel = homeViewModel

        setAdapter()
        setObservers()
    }
    //Adapter for listing items
    private fun setAdapter() {
        todoAdapter = TodoAdapter(this)
        completedTodoAdapter = TodoAdapter(this)
        binding.todo.adapter = todoAdapter
        binding.todos.adapter = completedTodoAdapter
    }

    private fun setObservers(){
        //Shows pending todos list
        homeViewModel.todo.observe(this, {
            it.let {
                todoAdapter.submitList(it)
            }
        })
        //Shows completed todos list
        homeViewModel.completedTodo.observe(this, {
            it.let {
                completedTodoAdapter.submitList(it)
            }
        })

        homeViewModel.eventTodo.observe(this, { clicked ->
            if (clicked) {
                val fragment = AddTodoFragment()
                fragment.show(supportFragmentManager, getString(R.string.tag))
                homeViewModel.onAddTodoClickComplete()
            }
        })
    }

    override fun onDeleteClicked(todo: Todo) {
        showDialog(todo)
    }

    override fun onEditClicked(todo: Todo) {
        showEditTodo(todo)
    }

    //Function will load a dialog fragment to edit the item
    private fun showEditTodo(todo: Todo) {
        val bundle = Bundle()
        val mData = Gson().toJson(todo)
        bundle.putString(TODO_UPDATE,mData.toString())
        val fragment = AddTodoFragment()
        fragment.arguments = bundle
        fragment.show(supportFragmentManager, getString(R.string.tag))
    }
    //Function called when a task is completed
    override fun onDoneClicked(todo: Todo) {
        todo.completed = !todo.completed
        homeViewModel.updateTodo(todo)
        todoAdapter.notifyDataSetChanged()
    }

    //Custom alert dialog screen to ask confirmation for deleting a task
    private fun showDialog(todo: Todo) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.delete_confirmation)
        val confirmDeleteButton = dialog.findViewById(R.id.confirm_button) as Button
        val cancelButton = dialog.findViewById(R.id.cancel_button) as Button
        confirmDeleteButton.setOnClickListener {
            homeViewModel.deleteTodo(todo)
            dialog.dismiss()
        }
        cancelButton.setOnClickListener { dialog.dismiss() }
        dialog.show()

    }
}