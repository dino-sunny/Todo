package com.dino.todo.home

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.dino.todo.R
import com.dino.todo.database.Todo
import com.dino.todo.database.TodoDatabase
import com.dino.todo.databinding.ActivityHomeBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
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
    private fun setAdapter() {
        todoAdapter = TodoAdapter(this)
        completedTodoAdapter = TodoAdapter(this)
        binding.todo.adapter = todoAdapter
        binding.todos.adapter = completedTodoAdapter
    }

    private fun setObservers(){
        homeViewModel.todo.observe(this, {
            it.let {
                todoAdapter.submitList(it)
            }
        })

        homeViewModel.completedTodo.observe(this, {
            it.let {
                completedTodoAdapter.submitList(it)
            }
        })

        homeViewModel.eventTodo.observe(this, { clicked ->
            if (clicked) {
                val fragment = AddTodoFragment()
                fragment.show(supportFragmentManager, "addTodo")
                homeViewModel.onAddTodoClickComplete()
            }
        })
    }

    override fun onDeleteClicked(todo: Todo) {
        showDialog(todo)
    }

    override fun onEditClicked(todo: Todo) {
        val bundle = Bundle()
        val mData = Gson().toJson(todo)
        bundle.putString("TODO_UPDATE",mData.toString())

        val fragment = AddTodoFragment()
        fragment.arguments = bundle
        fragment.show(supportFragmentManager, "addTodo")
    }

    override fun onDoneClicked(todo: Todo) {
        todo.completed = true
        homeViewModel.updateTodo(todo)
        todoAdapter.notifyDataSetChanged()
    }

    private fun showDialog(todo: Todo) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.delete_confirmation)
        val yesBtn = dialog.findViewById(R.id.confirm_button) as Button
        val noBtn = dialog.findViewById(R.id.cancel_button) as Button
        yesBtn.setOnClickListener {
            homeViewModel.deleteTodo(todo)
            dialog.dismiss()
        }
        noBtn.setOnClickListener { dialog.dismiss() }
        dialog.show()

    }
}