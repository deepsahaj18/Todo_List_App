package com.example.project2_todolistapp

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.project2_todolistapp.databinding.ActivityMainBinding
import com.example.project2_todolistapp.databinding.BottomSheetBinding
import com.example.project2_todolistapp.db.Todo
import com.example.project2_todolistapp.db.TodoListDb
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.util.Date
import kotlin.concurrent.thread

// TODO 4: Create a ViewHolder for the Recycler View
// TODO 5: Create an Adapter for the Recycler View
// TODO 6: Handle Click events on the ToDos
// TODO 7: Add a Floating Action Button
// TODO 8: Create a Dialog Box to create a ToDo (Bottom Sheet Optional)
// TODO 9: Build a DBHelper class with (Entities, DAOs, Database and TypeConverters)
// TODO 10: Push new ToDos in the DB
// TODO 11: Whenever the App is launched sync your data with DB

// Optional TODOs
// 1. Create a user login/signup flow
// 2. Add a side navigation bar
// 3. Add a profile section where users can set the profile (Profile Pic, Name, DOB, Bio, etc.)
// 4. Push all todos data in Firebase (if user logs in from another device)
// 5. Add search feature
// 6. Add filter by date feature
// 7. Add section in Recycler View, on the basis of Date
// 8. Add reminders on Todos that have a deadline
// 9. Add new screen to display the tasks that are done

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


        //Create a random to-do and push it to the db

        thread{
            database = Room.databaseBuilder(
                this@MainActivity,
                TodoListDb::class.java,
                "todolistDB"
            ).build()


            val listOfTodo=database.todoDao().fetchAllTodos()
            adapter.updateData(listOfTodo)



        }


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
