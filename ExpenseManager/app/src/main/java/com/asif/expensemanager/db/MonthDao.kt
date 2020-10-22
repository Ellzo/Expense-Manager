package com.asif.expensemanager.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface MonthDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(month: Month)

    @Update
    fun update(month: Month)

    @Delete
    fun delete(month: Month)

    @Query("SELECT * FROM months_table ORDER BY id DESC")
    fun getAll(): LiveData<List<Month>>

    @Query("SELECT * FROM months_table WHERE id = :id ORDER BY id DESC")
    fun getById(id: Int): LiveData<List<Month>>
}