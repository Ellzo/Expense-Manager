package com.asif.expensemanager.ui.activities

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.asif.expensemanager.R
import com.asif.expensemanager.background.db.Transaction
import com.asif.expensemanager.background.db.TransactionViewModel
import com.asif.expensemanager.background.utils.SharedPrefsUtils
import com.asif.expensemanager.ui.adapters.MonthYearAdapter
import com.asif.expensemanager.ui.adapters.TransactionsAdapter
import com.asif.expensemanager.ui.fragments.*

/**
 * The Main & Only Activity in the app
 */
class MainActivity : AppCompatActivity(),
    HomeFragment.HomeFragmentListener,
    TimePicker.OnTimeSetListener,
    DatePicker.OnDateSetListener,
    AddEditFragment.AddEditListener,
    AllTransactionsFragment.AllTransactionsListener,
    TransactionsAdapter.TransactionsAdapterListener,
    ReportFragment.ReportListener,
    MonthYearAdapter.MonthYearListener,
    MonthYearFragment.MonthYearFragListener {

    //Instances of the fragments
    private var addEditFragment: AddEditFragment? = null
    private var homeFragment: HomeFragment? = null
    private var allTransactionsFragment: AllTransactionsFragment? = null
    private var reportFragment: ReportFragment? = null

    //Handler, to handle splash screen replace
    private var mDelayHandler: Handler? = null

    //Runnable to replace the splash screen with other fragment
    private val mRunnable: Runnable = Runnable {
        if (!isFinishing) {
            //Get the user's name
            val name = SharedPrefsUtils.getUserName(this@MainActivity)

            //Check if it is already saved,
            // if not, show the OnBoardingFragment
            if (name == null || name.isEmpty()) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.container, OnBoardingFragment())
                    .commit()
            } else {
                if (homeFragment == null)
                    homeFragment = HomeFragment()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.container, homeFragment!!)
                    .commit()
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Show the splash screen fragment
        supportFragmentManager.beginTransaction()
            .add(R.id.container, SplashFragment())
            .commit()

        //Initialize the Handler
        mDelayHandler = Handler()

        //Navigate with delay
        mDelayHandler!!.postDelayed(mRunnable, 1500)
    }

    /*
    * Overridden method to show the Add Transaction screen
     */
    override fun onAddTransactionClicked() {
        addEditFragment = AddEditFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, addEditFragment!!)
            .addToBackStack(null)
            .commit()
    }

    /*
    * Overridden method to show AllTransactionsFragment
     */
    override fun onShowAllTransactions() {
        if (allTransactionsFragment == null) {
            allTransactionsFragment = AllTransactionsFragment()
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, allTransactionsFragment!!)
            .commit()
    }

    /*
    * Overridden method to show the Report fragment
     */
    override fun onReportSelected() {
        if (reportFragment == null) {
            reportFragment = ReportFragment()
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.container, reportFragment!!)
            .commit()

    }

    /*
    * Overridden method to notify the addEditFragment when time is picked
     */
    override fun onTimeSet(hour: Int, minute: Int) {
        if (addEditFragment != null) {
            addEditFragment!!.setTime(hour, minute)
        }
    }

    /*
    * Overridden method to notify the addEditFragment when date is picked
     */
    override fun onDateSet(isStartDate: Boolean, year: Int, month: Int, day: Int) {
        if (addEditFragment != null) {
            if (isStartDate) {
                addEditFragment!!.setStartDate(year, month, day)
            } else {
                addEditFragment!!.setEndDate(year, month, day)
            }
        }
    }

    /*
    * Overridden method to return home when data is added from "Add Transaction" fragment
     */
    override fun onDataAdded() {
        returnHome()
    }

    /*
    * Method to return to HomeFragment
     */
    private fun returnHome() {
        if (homeFragment == null) {
            homeFragment = HomeFragment()
        }
        supportFragmentManager.popBackStack()
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, homeFragment!!)
            .commit()
    }

    /*
    * Overridden method that handles Home selection, so it returns to HomeFragment
     */
    override fun onHomeSelected() {
        returnHome()
    }

    /*
    * Overridden method that handles the Transaction click event,
    *  it shows AddEditFragment with the transaction data
     */
    override fun onTransactionClick(transaction: Transaction) {
        addEditFragment = AddEditFragment.newInstance(transaction.id)

        supportFragmentManager.beginTransaction()
            .replace(R.id.container, addEditFragment!!)
            .addToBackStack(null)
            .commit()
    }

    /*
    * Overridden method that handles the Transaction long click event,
    *  it shows a dialog to ask the user wither to delete the transaction or not,
     */
    override fun onTransactionLongClick(transaction: Transaction) {
        //Create AlertDialog Builder
        AlertDialog.Builder(this@MainActivity)
            //Set dialog title
            .setTitle(getString(R.string.delete_tran))
            //Set dialog message
            .setMessage(getString(R.string.delete_question) + " " + transaction.name + getString(R.string.question_mark))
            //Set dialog negative button to "No", and its listener
            .setNegativeButton(R.string.no) { _, _ ->
                //Do nothing, just dismiss the dialog
            }.setPositiveButton(R.string.yes) { _, _ ->
                //Delete the transaction from the database if the positive button is clicked
                ViewModelProvider(this).get(TransactionViewModel::class.java)
                    .deleteTransaction(transaction)
            }
            //Create and show the dialog
            .create().show()
    }

    /*
    * Overridden method to handle click events on month/year cards
     */
    override fun onCardClick(
        monthYearName: String,
        balance: Float,
        expenses: Float,
        transactions: ArrayList<Transaction>
    ) {
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.container,
                MonthYearFragment.newInstance(monthYearName, balance, expenses, transactions)
            )
            .addToBackStack(null)
            .commit()
    }

    /*
    * Overridden method to show CalendarFragment
     */
    override fun showCalendar(monthName: String, transactions: ArrayList<Transaction>?) {
        if (transactions != null)
            supportFragmentManager.beginTransaction()
                .replace(
                    R.id.container,
                    CalendarFragment.newInstance(monthName, transactions)
                )
                .addToBackStack(null)
                .commit()
    }

    override fun onDestroy() {
        //Remove the handler's callback when the activity is being destroyed
        if (mDelayHandler != null) {
            mDelayHandler!!.removeCallbacks(mRunnable)
        }

        super.onDestroy()
    }
}