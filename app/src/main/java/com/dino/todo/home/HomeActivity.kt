package com.dino.todo.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.dino.todo.R
import com.dino.todo.database.TodoDatabase
import com.dino.todo.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var todoAdapter: TodoAdapter
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
        todoAdapter = TodoAdapter()
        binding.todo.adapter = todoAdapter
    }

    private fun setObservers(){
        homeViewModel.todo.observe(this,{
            it.let {
                todoAdapter.submitList(it)
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
}