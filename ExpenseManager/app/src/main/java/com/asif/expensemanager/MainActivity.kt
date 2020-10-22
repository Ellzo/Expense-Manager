package com.asif.expensemanager

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.asif.expensemanager.db.Transaction
import com.asif.expensemanager.db.TransactionViewModel

class MainActivity : AppCompatActivity(),
    HomeFragment.HomeFragmentListener,
    TimePicker.OnTimeSetListener,
    DatePicker.OnDateSetListener,
    AddEditFragment.AddEditListener,
    AllTransactionsFragment.AllTransactionsListener,
    TransactionsAdapter.TransactionsAdapterListener,
    ReportFragment.ReportListener,
    MonthYearAdapter.MonthYearListener {
    private var addEditFragment: AddEditFragment? = null
    private var homeFragment: HomeFragment? = null
    private var allTransactionsFragment: AllTransactionsFragment? = null
    private var reportFragment: ReportFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Get the user's name, and check if it is already saved,
        // if not, show the OnBoardingFragment
        val name = Utils.getUserName(this@MainActivity)
        if (name == null || name.isEmpty()) {
            supportFragmentManager.beginTransaction()
                .add(R.id.container, OnBoardingFragment())
                .commit()
        } else {
            if (homeFragment == null) {
                homeFragment = HomeFragment()
            }
            if (supportFragmentManager.backStackEntryCount == 0)
                supportFragmentManager.beginTransaction()
                    .add(R.id.container, homeFragment!!)
                    .commit()
        }
    }

    override fun onAddTransactionClicked() {
        addEditFragment = AddEditFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, addEditFragment!!)
            .addToBackStack(null)
            .commit()
    }

    override fun onShowAllTransactions() {
        if (allTransactionsFragment == null) {
            allTransactionsFragment = AllTransactionsFragment()
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, allTransactionsFragment!!)
            .commit()
    }

    override fun onReportSelected() {
        if (reportFragment == null) {
            reportFragment = ReportFragment()
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.container, reportFragment!!)
            .commit()

    }

    override fun onTimeSet(hour: Int, minute: Int) {
        if (addEditFragment != null) {
            addEditFragment!!.setTime(hour, minute)
        }
    }

    override fun onDateSet(isStartDate: Boolean, year: Int, month: Int, day: Int) {
        if (addEditFragment != null) {
            if (isStartDate) {
                addEditFragment!!.setStartDate(year, month, day)
            } else {
                addEditFragment!!.setEndDate(year, month, day)
            }
        }
    }

    override fun onDataAdded() {
        returnHome()
    }

    private fun returnHome() {
        if (homeFragment == null) {
            homeFragment = HomeFragment()
        }
        supportFragmentManager.popBackStack()
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, homeFragment!!)
            .commit()
    }

    override fun onHomeSelected() {
        returnHome()
    }

    override fun onTransactionClick(transaction: Transaction) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, AddEditFragment.newInstance(transaction.id))
            .addToBackStack(null)
            .commit()
    }

    override fun onTransactionLongClick(transaction: Transaction) {
        AlertDialog.Builder(this@MainActivity)
            .setTitle(getString(R.string.delete_tran))
            .setMessage(getString(R.string.delete_question) + transaction.name + getString(R.string.question_mark))
            .setNegativeButton(R.string.no) { _, _ ->
                //Do nothing, just dismiss the dialog
            }.setPositiveButton(R.string.yes) { _, _ ->
                ViewModelProvider(this).get(TransactionViewModel::class.java)
                    .deleteTransaction(transaction)
            }.create().show()
    }

    override fun onCardClick(
        balance: Float,
        expenses: Float,
        transactions: ArrayList<Transaction>
    ) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, MonthYearFragment.newInstance(balance, expenses, transactions))
            .addToBackStack(null)
            .commit()
    }
}