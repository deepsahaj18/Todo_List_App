package com.example.project2_todolistapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.project2_todolistapp.databinding.TodoListItemBinding
import com.example.project2_todolistapp.db.Todo
import java.text.SimpleDateFormat
import java.util.Locale

class TodoListViewHolder(private val itemBinding: TodoListItemBinding) : RecyclerView.ViewHolder(itemBinding.root) {
    fun bindData(todo: Todo) {
        itemBinding.itemTodoTitle.text = todo.title
        itemBinding.tvItemTodoDesc.text = todo.desc
        itemBinding.tvItemTodoDate.text = todo.date.toString()
        val dateFormat = SimpleDateFormat("EEE, MMM dd HH:mm", Locale.getDefault())
        val formattedDate = dateFormat.format(todo.date)
        itemBinding.tvItemTodoDate.text = formattedDate
    }
}

class TodoListAdapter(
    var listOfTodos: MutableList<Todo>,
    private val onDeleteClick: (Todo) -> Unit
) : RecyclerView.Adapter<TodoListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoListViewHolder {
        val itemBinding =
            TodoListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TodoListViewHolder(itemBinding)
    }

    override fun getItemCount() = listOfTodos.size

    override fun onBindViewHolder(holder: TodoListViewHolder, position: Int) {
        holder.bindData(listOfTodos[position])
        holder.itemView.setOnClickListener {
            // Handle click events on todos if needed
        }
    }

    fun getItemAtPosition(position: Int): Todo {
        return listOfTodos[position]
    }

    fun deleteItem(position: Int) {
        onDeleteClick(listOfTodos[position])
    }

    fun updateData(newList: MutableList<Todo>) {
        listOfTodos = newList
        notifyDataSetChanged()
    }

    fun addNewItem(todo: Todo) {
        listOfTodos.add(0, todo)
        notifyItemInserted(0)
    }
}

fun RecyclerView.addSwipeToDelete(deleteAction: (Int) -> Unit) {
    val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
        0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
    ) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return false // We don't support move action
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.adapterPosition
            deleteAction(position)
        }
    })
    itemTouchHelper.attachToRecyclerView(this)
}
