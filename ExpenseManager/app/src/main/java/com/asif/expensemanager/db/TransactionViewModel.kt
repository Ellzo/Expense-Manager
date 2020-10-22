package com.asif.expensemanager.db

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.asif.expensemanager.R
import com.asif.expensemanager.Utils

class TransactionViewModel(val app: Application) : AndroidViewModel(app) {
    private val repo: TransactionRepository = TransactionRepository(app)
    private var allTransactions: LiveData<List<Transaction>>

    init {
        allTransactions = repo.getAllTransactions()
    }

    fun getTransactionById(id: Int): LiveData<Transaction> = repo.getTransactionById(id)

    fun getAllTransactions(): LiveData<List<Transaction>> = allTransactions

    fun getUpcomingTransactions(count: Int, date: Long): LiveData<List<Transaction>> =
        repo.getUpcomingTransactions(count, date)

    fun getPastTransactions(date: Long): LiveData<List<Transaction>> =
        repo.getPastTransactions(date)

    fun searchTransaction(keyword: String): LiveData<List<Transaction>> =
        repo.searchTransaction(keyword)

    fun searchTransactionByCategory(
        category: String,
        keyword: String
    ): LiveData<List<Transaction>> = repo.searchTransactionByCategory(category, keyword)

    fun insertTransaction(transaction: Transaction) = repo.insertTransaction(transaction)

    fun updateTransaction(transaction: Transaction) = repo.updateTransaction(transaction)

    fun deleteTransaction(transaction: Transaction) = repo.deleteTransaction(transaction)
}
