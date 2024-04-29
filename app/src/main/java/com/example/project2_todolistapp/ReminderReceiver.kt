import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.project2_todolistapp.R

class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val sharedPreferences =
            context.getSharedPreferences("ReminderPrefs", Context.MODE_PRIVATE)

        val todoId = intent.getLongExtra("todo_id", -1)
        if (todoId != -1L) {
            // Use the todo ID to create a unique notification ID
            val notificationId = (todoId % Int.MAX_VALUE).toInt() // Ensure the ID is within the valid range

            // Check if the app has notification access permission
            if (context.checkSelfPermission(android.Manifest.permission.ACCESS_NOTIFICATION_POLICY) == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted, create and show the notification
                createNotificationChannel(context)
                showNotification(context, notificationId)
            } else {
                // Permission is not granted, handle accordingly
            }
        }
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Reminder Channel"
            val descriptionText = "Channel for reminder notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("reminder_channel", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showNotification(context: Context, reminderId: Int) {
        val builder = NotificationCompat.Builder(context, "reminder_channel")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Reminder Title")
            .setContentText("Reminder Content")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(context)) {
            notify(reminderId, builder.build()) // Use unique ID for each reminder
        }
    }
}
