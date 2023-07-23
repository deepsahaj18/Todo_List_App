package com.example.project2_todolistapp.db

import androidx.room.TypeConverter
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date

//3. TypeConverter-> used to add custom object in the db (eg- date)

class DateConverter {

    //To convert date to long -> push data into the db
    @TypeConverter
    fun fromDateToLong(date: Date): Long {
        return date.time
    }


    //To convert long to date -> reading date from the db
    @TypeConverter
    fun fromLongToDate(timestamp: Long): Date {
        return Date(timestamp)
//        return DateFormat.getDateTimeInstance().format(timestamp)


    }
}