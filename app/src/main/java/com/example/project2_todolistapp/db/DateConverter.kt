package com.example.project2_todolistapp.db

import androidx.room.TypeConverter
import java.text.SimpleDateFormat
import java.util.Date

class DateConverter {

    // Format pattern for date and time
    private val dateFormat = SimpleDateFormat("EEE, dd MMM yyyy 'at' HH:mm")

    // To convert date to long -> push data into the db
    @TypeConverter
    fun fromDateToLong(date: Date): Long {
        return date.time
    }

    // To convert long to date -> reading date from the db
    @TypeConverter
    fun fromLongToDate(timestamp: Long): Date {
        // Convert timestamp to Date
        return Date(timestamp)
    }
}
