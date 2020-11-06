package com.asif.expensemanager.background.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Abstract Class, extending RoomDatabase and representing the app's database.
 * The db consists of a single table, which is transactions table.
 **/
@Database(entities = [Transaction::class], version = 1, exportSchema = false)
abstract class ExpensesDB : RoomDatabase() {

    //Data access object
    abstract fun transactionDao(): TransactionDao

    companion object {

        @Volatile
        //Database instance
        private var instance: ExpensesDB? = null

        /*
        * Static method to get an instance of the database
         */
        fun getInstance(context: Context): ExpensesDB {
            // Singleton prevents multiple instances of database opening at the
            // same time.
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        instance = Room.databaseBuilder(
                            context.applicationContext,
                            ExpensesDB::class.java,
                            "expense_database"
                        ).build()
                    }
                }
            }
            return instance!!
        }
    }
}