package com.example.project2_todolistapp

import ReminderReceiver
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.project2_todolistapp.databinding.ActivityMainBinding
import com.example.project2_todolistapp.databinding.BottomSheetBinding
import com.example.project2_todolistapp.db.Todo
import com.example.project2_todolistapp.db.TodoListDb
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlin.concurrent.thread
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var database: TodoListDb
    private lateinit var adapter: TodoListAdapter
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("YourSharedPreferencesName", Context.MODE_PRIVATE)
        val userName = sharedPreferences.getString("user_name", "")

        if (userName.isNullOrEmpty()) {
            // Prompt user for name
            promptForUserName()
        } else {
            displayWelcomeMessage(userName)
        }

        adapter = TodoListAdapter(mutableListOf()) { todo ->
            deleteTodo(todo)
        }
        binding.rvToDoList.layoutManager = LinearLayoutManager(this@MainActivity, RecyclerView.VERTICAL, false)
        binding.rvToDoList.adapter = adapter

        // Check if the todo list is empty
        if (adapter.itemCount == 0) {
            // Show welcome message
            binding.tvWelcomeMessage.visibility = View.VISIBLE
        }

        binding.fabAddTodo.setOnClickListener {
            showBottomSheet()
        }

        thread {

            database = Room.databaseBuilder(
                this@MainActivity,
                TodoListDb::class.java,
                "todolistDB"
            ).build()

            val listOfTodo = database.todoDao().fetchAllNonDeletedTodos()
            runOnUiThread {
                adapter.updateData(listOfTodo)
                if (listOfTodo.isNotEmpty()) {
                    binding.tvWelcomeMessage.visibility = View.GONE
                }
            }
        }

        binding.btnRecycleBin.setOnClickListener {
            startActivity(Intent(this, RecycleBinActivity::class.java))
        }
        binding.fabDelete.setOnClickListener {
            deleteAllTodoItems()
        }

        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val todo = adapter.getItemAtPosition(position)
                deleteTodo(todo)
            }
        })
        itemTouchHelper.attachToRecyclerView(binding.rvToDoList)
    }







    private fun addNewItem(todo: Todo) {
        adapter.addNewItem(todo)
        if (adapter.itemCount > 0) {
            binding.tvWelcomeMessage.visibility = View.GONE
        }
    }

    private fun deleteTodo(todo: Todo) {
        thread {
            todo.deleted = true
            database.todoDao().updateTodo(todo) // Mark the todo as deleted
            val listOfTodo = database.todoDao().fetchAllNonDeletedTodos()
            runOnUiThread {
                adapter.updateData(listOfTodo)
                if (listOfTodo.isEmpty()) {
                    binding.tvWelcomeMessage.visibility = View.VISIBLE
                }
            }
        }
    }




    private fun deleteAllTodoItems() {
        thread {
            val allTodos = database.todoDao().fetchAllTodos()
            allTodos.forEach { it.deleted = true }
            database.todoDao().updateTodos(allTodos) // Mark all todos as deleted
            val listOfTodo = database.todoDao().fetchAllNonDeletedTodos()
            runOnUiThread {
                adapter.updateData(listOfTodo)
                if (listOfTodo.isEmpty()) {
                    binding.tvWelcomeMessage.visibility = View.VISIBLE
                }
            }
        }
    }


    private fun showBottomSheet() {
        val bottomSheet = BottomSheetBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(this)


        dialog.setContentView(bottomSheet.root)

        bottomSheet.btn.setOnClickListener {
            if (bottomSheet.tietTitle.text.isNullOrBlank()) {
                bottomSheet.tietTitle.error = "Can't Be Empty"
                return@setOnClickListener
            }
            if (bottomSheet.tietDesc.text.isNullOrBlank()) {
                bottomSheet.tietDesc.error = "Can't Be Empty"
                return@setOnClickListener
            }
            val todo = Todo(
                title = bottomSheet.tietTitle.text.toString(),
                desc = bottomSheet.tietDesc.text.toString(),
                date = Date(System.currentTimeMillis())
            )

            addNewItem(todo)
            thread {
                database.todoDao().insertTodo(todo)
            }
            dialog.dismiss()
        }
//        bottomSheet.switch1.setOnCheckedChangeListener { _, isChecked ->
//
//            if (isChecked) {
//            // Switch is turned on, show reminder dialog
//            showReminderDialog()
////                Toast.makeText(this,"hello", Toast.LENGTH_LONG).show()
//        } else {
//            // Switch is turned off, handle accordingly
//        }
//        }
        dialog.show()
    }

//    private fun showReminderDialog() {
//
//
//        val timePickerDialog = TimePickerDialog(
//            this,
//            R.style.SpinnerTimePickerDialog,
//            { _, hours, minutes ->
//                // Convert the selected duration to milliseconds
//                val durationMillis = (hours * 60 * 60 * 1000) + (minutes * 60 * 1000)
//
//                // Schedule a notification after the selected duration
//                scheduleNotification(durationMillis)
//            },
//            0, 0, true
//        )
//
//        // Set transparent background
//        timePickerDialog.window?.setBackgroundDrawableResource(R.drawable.time_background)
//        timePickerDialog.setTitle("Remind in")
//        timePickerDialog.show()
//    }
//    private fun scheduleNotification(durationMillis: Int) {
//        // Get AlarmManager instance
//        val durationMillisLong = durationMillis.toLong()
//        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
//
//        // Create an intent for the BroadcastReceiver
//        val intent = Intent(this, ReminderReceiver::class.java)
//        val pendingIntent = PendingIntent.getBroadcast(
//            this,
//            0,
//            intent,
//            PendingIntent.FLAG_IMMUTABLE // Specify FLAG_IMMUTABLE for Android 12 and above
//
//        )
//
//        // Calculate the time when the notification should be shown
//        val triggerTime = System.currentTimeMillis() + durationMillisLong
//
//        // Set the alarm using AlarmManager
//        alarmManager.setExact(
//            AlarmManager.RTC_WAKEUP,
//            triggerTime,
//            pendingIntent
//        )
//    }



    private fun promptForUserName() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_custom_name, null)
        val editTextName = dialogView.findViewById<EditText>(R.id.editTextName)
        val btnOk = dialogView.findViewById<Button>(R.id.btnOk)

        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()


        btnOk.setOnClickListener {
            val userName = editTextName.text.toString().trim()
            if (userName.isNullOrBlank()) {
                return@setOnClickListener
            }

            sharedPreferences.edit().putString("user_name", userName).apply()

            displayWelcomeMessage(userName)
            editTextName.clearFocus()
            dialogBuilder.dismiss()
        }

        dialogBuilder.show()
    }

    private fun displayWelcomeMessage(userName: String) {
        binding.tvWelcomeMessage.text = "Welcome, $userName. \n Let's make today productive!"
    }


}
