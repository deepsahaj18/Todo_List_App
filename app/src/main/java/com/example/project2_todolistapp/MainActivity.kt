package com.example.project2_todolistapp

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.room.Room
import com.example.project2_todolistapp.databinding.ActivityMainBinding
import com.example.project2_todolistapp.databinding.BottomSheetBinding
import com.example.project2_todolistapp.db.Todo
import com.example.project2_todolistapp.db.TodoListDb
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.util.Date
import kotlin.concurrent.thread
class MainActivity : AppCompatActivity() {

    private lateinit var binding:ActivityMainBinding
    private lateinit var database: TodoListDb
    private lateinit var adapter: TodoListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        with(window) {
            setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }

        binding= ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        adapter= TodoListAdapter(mutableListOf())
        binding.rvToDoList.layoutManager=LinearLayoutManager(this@MainActivity, RecyclerView.VERTICAL,false)
        binding.rvToDoList.adapter=adapter


        binding.fabAddTodo.setOnClickListener{
            showBottomSheet()

        }

        thread{
            database = Room.databaseBuilder(
                this@MainActivity,
                TodoListDb::class.java,
                "todolistDB"
            ).build()


            val listOfTodo=database.todoDao().fetchAllTodos()
            adapter.updateData(listOfTodo)

        }

        binding.fabDelete.setOnClickListener {
            deleteIt()
        }
    }
    
    private fun deleteIt(){
        val listOfTodo= mutableListOf<Todo>()

        thread {
            database.todoDao().deleteAll()
            val listOfTodo = database.todoDao().fetchAllTodos()
        }

        adapter.updateData(listOfTodo)

    }

    private fun showBottomSheet(){
        val bottomSheet=BottomSheetBinding.inflate(layoutInflater)
        val dialog=BottomSheetDialog(this)

        dialog.setContentView(bottomSheet.root)

        bottomSheet.btn.setOnClickListener {
            //ADD TO-DO IN THE DB
            if (bottomSheet.tietTitle.text.isNullOrBlank()){
                bottomSheet.tietTitle.error="Can't Be Empty"
                return@setOnClickListener
            }
            if (bottomSheet.tietDesc.text.isNullOrBlank()){
                bottomSheet.tietDesc.error="Can't Be Empty"
                return@setOnClickListener
            }
            val todo=Todo(
                title=bottomSheet.tietTitle.text.toString(),
                desc = bottomSheet.tietDesc.text.toString(),
                date = Date(System.currentTimeMillis())
            )

            adapter.addNewItem(todo)
            thread {
                database.todoDao().insertTodo(todo)
            }
            dialog.dismiss()
        }
        dialog.show()
    }
}




// TODO 4: Create a ViewHolder for the Recycler View
// TODO 5: Create an Adapter for the Recycler View
// TODO 6: Handle Click events on the ToDos
// TODO 7: Add a Floating Action Button
// TODO 8: Create a Dialog Box to create a ToDo (Bottom Sheet Optional)
// TODO 9: Build a DBHelper class with (Entities, DAOs, Database and TypeConverters)
// TODO 10: Push new ToDos in the DB
// TODO 11: Whenever the App is launched sync your data with DB
