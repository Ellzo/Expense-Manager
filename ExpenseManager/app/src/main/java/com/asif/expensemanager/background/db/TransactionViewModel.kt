package com.asif.expensemanager.background.db

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData

/**
 * ViewModel class, which uses LiveData, to keep the data presented on the UI always Up-To-Date.
 */
class TransactionViewModel(val app: Application) : AndroidViewModel(app) {
    //Transactions Repository instance
    private val repo: TransactionRepository = TransactionRepository(app)

    //All transactions list, wrapped in a LiveData object
    private var allTransactions: LiveData<List<Transaction>>

    /*
    * Initialize the all transactions list
     */
    init {
        allTransactions = repo.getAllTransactions()
    }

    /*
    * Method to get a specific transaction by its id.
     */
    fun getTransactionById(id: Int): LiveData<Transaction> = repo.getTransactionById(id)

    /*
    * Method to get all transactions.
     */
    fun getAllTransactions(): LiveData<List<Transaction>> = allTransactions

    /*
    * Method to get upcoming transactions.
     */
    fun getUpcomingTransactions(count: Int, date: Long): LiveData<List<Transaction>> =
        repo.getUpcomingTransactions(count, date)

    /*
    * Method to get past transactions.
     */
    fun getPastTransactions(date: Long): LiveData<List<Transaction>> =
        repo.getPastTransactions(date)

    /*
    * Method to search in all transactions.
     */
    fun searchTransaction(keyword: String): LiveData<List<Transaction>> =
        repo.searchTransaction(keyword)

    /*
    * Method to search in the transactions of a specific category.
     */
    fun searchTransactionByCategory(
        category: String,
        keyword: String
    ): LiveData<List<Transaction>> = repo.searchTransactionByCategory(category, keyword)

    /*
    * Method to insert a transaction.
     */
    fun insertTransaction(transaction: Transaction) = repo.insertTransaction(transaction)

    /*
    * Method to update transaction.
     */
    fun updateTransaction(transaction: Transaction) = repo.updateTransaction(transaction)

    /*
    * Method to delete a transaction.
     */
    fun deleteTransaction(transaction: Transaction) = repo.deleteTransaction(transaction)
}
