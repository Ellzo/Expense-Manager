package com.asif.expensemanager.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(item: Transaction)

    @Update
    fun update(item: Transaction)

    @Delete
    fun delete(item: Transaction)

    @Query("SELECT * FROM transactions_table WHERE id = :id;")
    fun getById(id: Int): LiveData<Transaction>

    @Query("SELECT * FROM transactions_table ORDER BY end_date DESC;")
    fun getAll(): LiveData<List<Transaction>>

    @Query("SELECT * FROM transactions_table WHERE start_date > :date OR end_date > :date ORDER BY start_date LIMIT :count;")
    fun getUpcoming(count: Int, date: Long): LiveData<List<Transaction>>

    @Query("SELECT * FROM transactions_table WHERE start_date <= :date;")
    fun getPast(date: Long): LiveData<List<Transaction>>

    @Query("SELECT * FROM transactions_table WHERE name LIKE '%' || :keyword || '%' ORDER BY start_date DESC;")
    fun search(keyword: String): LiveData<List<Transaction>>

    @Query("SELECT * FROM transactions_table WHERE category = :category AND name LIKE '%' || :keyword || '%' ORDER BY start_date DESC;")
    fun searchByCategory(category: String, keyword: String): LiveData<List<Transaction>>

}