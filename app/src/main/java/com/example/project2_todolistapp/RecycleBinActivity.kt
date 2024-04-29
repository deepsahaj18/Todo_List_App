package com.example.project2_todolistapp

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.project2_todolistapp.databinding.ActivityRecycleBinBinding
import com.example.project2_todolistapp.db.Todo
import com.example.project2_todolistapp.db.TodoListDb
import java.util.*
import kotlin.concurrent.thread

class RecycleBinActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecycleBinBinding
    private lateinit var database: TodoListDb
    private lateinit var adapter: TodoListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        binding = ActivityRecycleBinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = TodoListAdapter(mutableListOf()) { todo ->
            // You can implement restore or permanent delete functionality here if needed
        }
        binding.rvRecycleBin.layoutManager = LinearLayoutManager(this@RecycleBinActivity, RecyclerView.VERTICAL, false)
        binding.rvRecycleBin.adapter = adapter

        thread {
            database = Room.databaseBuilder(
                this@RecycleBinActivity,
                TodoListDb::class.java,
                "todolistDB"
            ).build()

            val listOfDeletedTodos = fetchDeletedTodos()
            runOnUiThread {
                adapter.updateData(listOfDeletedTodos)
            }
        }



        binding.fabDeleteAll.setOnClickListener {
            deleteAllTodosInRecycleBin()
        }

    }

    private fun fetchDeletedTodos(): MutableList<Todo> {
        // Fetch all deleted todos from the database
        return database.todoDao().fetchDeletedTodos()
    }
    private fun deleteAllTodosInRecycleBin() {
        thread {
            // Fetch all deleted todos and delete them permanently from the database
            val deletedTodos = database.todoDao().fetchDeletedTodos()
            deletedTodos.forEach { database.todoDao().deleteTodo(it) }
            // Fetch and display the updated list of todos in the RecycleBin
            val updatedTodos = database.todoDao().fetchDeletedTodos()
            runOnUiThread {
                adapter.updateData(updatedTodos)
            }
        }
    }


}
