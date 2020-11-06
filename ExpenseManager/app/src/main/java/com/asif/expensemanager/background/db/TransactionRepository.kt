package com.asif.expensemanager.background.db

import android.app.Application
import android.os.AsyncTask
import androidx.lifecycle.LiveData

/**
 * Repository class to handle database operations from different data sources, & to ensure abstraction
 * In this app, there is only one data source,
 * but it can facilitate communication with other data sources, when created.
 */
class TransactionRepository(application: Application) {
    //Transactions Data Access Object
    private val transactionDao: TransactionDao =
        ExpensesDB.getInstance(application).transactionDao()


    fun getTransactionById(id: Int): LiveData<Transaction> = transactionDao.getById(id)

    fun getAllTransactions(): LiveData<List<Transaction>> = transactionDao.getAll()

    fun getUpcomingTransactions(count: Int, date: Long): LiveData<List<Transaction>> =
        transactionDao.getUpcoming(count, date)

    fun getPastTransactions(date: Long): LiveData<List<Transaction>> = transactionDao.getPast(date)

    fun searchTransaction(keyword: String): LiveData<List<Transaction>> =
        transactionDao.search(keyword)

    fun searchTransactionByCategory(
        category: String,
        keyword: String
    ): LiveData<List<Transaction>> = transactionDao.searchByCategory(category, keyword)

    fun insertTransaction(transaction: Transaction) {
        InsertTransactionAsyncTask(transactionDao).execute(transaction)
    }

    fun updateTransaction(transaction: Transaction) {
        UpdateTransactionAsyncTask(transactionDao).execute(transaction)
    }

    fun deleteTransaction(transaction: Transaction) {
        DeleteTransactionAsyncTask(transactionDao).execute(transaction)
    }

    /**
     * Companion object containing AsyncTask classes,
     * to do database operations on a background thread.
     */
    companion object {
        internal class InsertTransactionAsyncTask(private val dao: TransactionDao) :
            AsyncTask<Transaction, Unit, Unit>() {
            override fun doInBackground(vararg params: Transaction?) {
                for (param in params) {
                    if (param != null) {
                        dao.insert(param)
                    }
                }
            }

        }

        internal class UpdateTransactionAsyncTask(private val dao: TransactionDao) :
            AsyncTask<Transaction, Unit, Unit>() {
            override fun doInBackground(vararg params: Transaction?) {
                for (param in params) {
                    if (param != null) {
                        dao.update(param)
                    }
                }
            }

        }

        internal class DeleteTransactionAsyncTask(private val dao: TransactionDao) :
            AsyncTask<Transaction, Unit, Unit>() {
            override fun doInBackground(vararg params: Transaction?) {
                for (param in params) {
                    if (param != null) {
                        dao.delete(param)
                    }
                }
            }

        }
    }
}