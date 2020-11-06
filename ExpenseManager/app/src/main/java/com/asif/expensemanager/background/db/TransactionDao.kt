package com.asif.expensemanager.background.db

import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * Data Access Object, used to perform operations on the database
 */
@Dao
interface TransactionDao {
    /*
    * Method to insert a transaction.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(item: Transaction)

    /*
    * Method to update a transaction.
     */
    @Update
    fun update(item: Transaction)

    /*
    * Method to delete a specific transaction.
     */
    @Delete
    fun delete(item: Transaction)

    /*
    * Method to get a specific transaction, using its id.
     */
    @Query("SELECT * FROM transactions_table WHERE id = :id;")
    fun getById(id: Int): LiveData<Transaction>

    /*
    * Method to get all transactions.
     */
    @Query("SELECT * FROM transactions_table ORDER BY end_date DESC;")
    fun getAll(): LiveData<List<Transaction>>

    /*
    * Method to get upcoming transactions.
     */
    @Query("SELECT * FROM transactions_table WHERE start_date > :date OR end_date > :date ORDER BY start_date LIMIT :count;")
    fun getUpcoming(count: Int, date: Long): LiveData<List<Transaction>>

    /*
    * Method to get past transactions.
     */
    @Query("SELECT * FROM transactions_table WHERE start_date <= :date ORDER BY start_date;")
    fun getPast(date: Long): LiveData<List<Transaction>>

    /*
    * Method to search in all transactions.
     */
    @Query("SELECT * FROM transactions_table WHERE name LIKE '%' || :keyword || '%' OR type LIKE '%' || :keyword || '%' OR comments LIKE '%' || :keyword || '%' ORDER BY start_date DESC;")
    fun search(keyword: String): LiveData<List<Transaction>>

    /*
    * Method to search in transactions of a specific category.
     */
    @Query("SELECT * FROM transactions_table WHERE category = :category AND name LIKE '%' || :keyword || '%' ORDER BY start_date DESC;")
    fun searchByCategory(category: String, keyword: String): LiveData<List<Transaction>>

}