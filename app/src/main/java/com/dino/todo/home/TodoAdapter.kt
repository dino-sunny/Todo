package com.dino.todo.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dino.todo.database.Todo
import com.dino.todo.databinding.TodoItemBinding

class TodoAdapter(private val todoClickListener: OnTodoClickListener) : ListAdapter<Todo, ViewHolder>(
    RecentDiffCallback()
){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = TodoItemBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind((item),todoClickListener)
    }
}

class ViewHolder(val binding: TodoItemBinding): RecyclerView.ViewHolder(binding.root){

    fun bind(item: Todo,todoClickListener: OnTodoClickListener) {
        binding.todo = item
        binding.clickListener = todoClickListener
        binding.executePendingBindings()
    }
}

//Listeners for button clicks
interface OnTodoClickListener{
    fun onDeleteClicked(todo: Todo)
    fun onEditClicked(todo: Todo)
    fun onDoneClicked(todo: Todo)
}
class RecentDiffCallback : DiffUtil.ItemCallback<Todo>() {
    override fun areItemsTheSame(oldItem: Todo, newItem: Todo): Boolean {
        return oldItem.todoId == newItem.todoId
    }

    override fun areContentsTheSame(oldItem: Todo, newItem: Todo): Boolean {
        return oldItem == newItem
    }
}