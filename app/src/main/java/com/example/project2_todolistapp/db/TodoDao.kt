package com.example.project2_todolistapp.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


//2. DAO -> DATA ACCESS OBJECT-> HELPS U IN ACCESSING THE DB WITHOUT WRITING A KOT OF CODE


@Dao
interface TodoDao {
    // CRUD

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTodo(todo: Todo)

    @Query("select * from Notes_table")
    fun fetchAllTodos():MutableList<Todo>

    @Query("DELETE FROM Notes_table")
    fun deleteAll()






}