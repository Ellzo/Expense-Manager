package com.asif.expensemanager.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Transaction::class, Month::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class ExpensesDB: RoomDatabase(){
    abstract fun transactionDao(): TransactionDao
    abstract fun monthDao(): MonthDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile private var instance: ExpensesDB? = null

        fun getInstance(context: Context): ExpensesDB{
            if(instance == null){
                synchronized(this) {
                    if(instance == null) {
                        instance = Room.databaseBuilder(context.applicationContext,
                        ExpensesDB::class.java,
                        "expense_database").build()
                    }
                }
            }
            return instance!!
        }
    }
}