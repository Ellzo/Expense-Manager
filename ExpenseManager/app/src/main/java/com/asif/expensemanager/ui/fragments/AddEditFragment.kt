package com.asif.expensemanager.ui.fragments

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.asif.expensemanager.R
import com.asif.expensemanager.background.db.Transaction
import com.asif.expensemanager.background.db.TransactionViewModel
import com.asif.expensemanager.background.utils.DateUtils
import com.asif.expensemanager.background.utils.SharedPrefsUtils
import kotlinx.android.synthetic.main.fragment_add_edit.*
import kotlinx.android.synthetic.main.fragment_add_edit.view.*
import java.text.DateFormat
import java.util.*

/**
 * Fragment that represents the screen in which the user can add or edit a Transaction.
 */
class AddEditFragment : Fragment() {
    /**
     * A listener for this fragment.
     */
    interface AddEditListener {
        fun onDataAdded()
    }

    //Global variable that holds the start minute of the transaction
    private var startMinute = 0

    //Global variable that holds the start hour of the transaction
    private var startHour = 0

    //Global variable that holds the start date, in millis, of the transaction
    private var startDate: Long = 0

    //Global variable that holds the end date of the transaction
    private var endDate: Long = 0

    //The root view
    private var root: View? = null

    //An instance of the listener
    private var listener: AddEditListener? = null


    override fun onAttach(context: Context) {
        //Initialize the listener, if the context in an instance of AddEditListener
        if (context is AddEditListener) {
            listener = context
        }

        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_add_edit, container, false)

        //Setup the spinners
        setupSpinners()

        //Set toolbar attributes
        root!!.toolbar.setTitleTextColor(Color.WHITE)
        root!!.toolbar.setNavigationIcon(R.drawable.ic_clear)
        root!!.toolbar.setNavigationOnClickListener {
            if (activity != null) {
                activity!!.supportFragmentManager.popBackStackImmediate()
            }
        }

        //Change visibilities, according to whither the "Recurring" CheckBox is checked or not
        root!!.checkRecurring.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                root!!.linearTime.visibility = View.INVISIBLE
                root!!.linearEndDate.visibility = View.VISIBLE
                root!!.tvDateFrom.text = getString(R.string.from)
                root!!.tvTimeTo.text = getString(R.string.to)
            } else {
                root!!.linearTime.visibility = View.VISIBLE
                root!!.linearEndDate.visibility = View.GONE
                root!!.tvDateFrom.text = getString(R.string.date)
                root!!.tvTimeTo.text = getString(R.string.time)
            }
        }

        //get Calendar instance
        val c = Calendar.getInstance()

        //If there is arguments passed to this fragment, the initial data of the input views,
        // else, set only the date and time input views to some initial value.
        // Also, change the toolbar's title accordingly
        if (arguments != null && arguments!!.containsKey(ID_KEY)) {
            root!!.toolbar.title = getString(R.string.edit_tran)
            val viewModel = ViewModelProvider(this).get(TransactionViewModel::class.java)
            viewModel.getTransactionById(arguments!!.getInt(ID_KEY))
                .observe(viewLifecycleOwner, Observer {
                    root!!.inputName.editText?.setText(it.name)
                    if (it.amount < 0) {
                        root!!.inputAmount.editText?.setText((it.amount * -1f).toString())
                    } else {
                        root!!.inputAmount.editText?.setText(it.amount.toString())
                    }
                    root!!.spinnerCat.setSelection(
                        getSpinnerPosition(
                            root!!.spinnerCat,
                            it.category!!
                        )
                    )
                    root!!.spinnerType.setSelection(
                        getSpinnerPosition(
                            root!!.spinnerType,
                            it.type!!
                        )
                    )
                    startDate = it.startDate
                    endDate = it.endDate
                    root!!.inputComments.editText?.setText(it.comments)
                    root!!.tvEndDate.text = DateFormat.getDateInstance().format(Date(endDate))
                    c.timeInMillis = startDate
                    startHour = c[Calendar.HOUR_OF_DAY]
                    startMinute = c[Calendar.MINUTE]
                    root!!.tvDateVal.text = DateFormat.getDateInstance().format(Date(startDate))
                    root!!.tvTimeVal.text =
                        DateFormat.getTimeInstance(DateFormat.SHORT).format(Date(startDate))
                    if (startDate != endDate) {
                        root!!.checkRecurring.isChecked = true
                    }
                })
        } else {
            root!!.toolbar.title = getString(R.string.add_tran)
            startDate = DateUtils.getCurrentTimestamp()
            c.timeInMillis = startDate
            startHour = c[Calendar.HOUR_OF_DAY]
            startMinute = c[Calendar.MINUTE]
            endDate = startDate
            val initialDate = DateFormat.getDateInstance().format(Date(startDate))
            root!!.tvDateVal.text = initialDate
            root!!.tvEndDate.text = initialDate
            root!!.tvTimeVal.text =
                DateFormat.getTimeInstance(DateFormat.SHORT).format(Date(startDate))
        }

        //Set an listener to "Income" button, that calls validateAndSaveData() method
        root!!.btnIncome.setOnClickListener {
            validateAndSaveData(true)
        }

        //Set an listener to "Expense" button, that calls validateAndSaveData() method
        root!!.btnExpense.setOnClickListener {
            validateAndSaveData(false)
        }

        //Show a dialog to let the user choose a start date, when the start date linear layout is clicked
        root!!.linearDate.setOnClickListener {
            c.timeInMillis = startDate
            DatePicker.newInstance(
                true,
                c[Calendar.YEAR],
                c[Calendar.MONTH],
                c[Calendar.DAY_OF_MONTH]
            ).show(childFragmentManager, "datePicker")
        }

        //Show a dialog to let the user choose a start time, when the start time linear layout is clicked
        root!!.linearTime.setOnClickListener {
            TimePicker.newInstance(startHour, startMinute).show(childFragmentManager, "timePicker")
        }

        //Show a dialog to let the user choose a end date, when the end date linear layout is clicked
        root!!.linearEndDate.setOnClickListener {
            c.timeInMillis = endDate
            DatePicker.newInstance(
                false,
                c[Calendar.YEAR],
                c[Calendar.MONTH],
                c[Calendar.DAY_OF_MONTH]
            ).show(childFragmentManager, "datePicker")
        }

        //Return the root view
        return root
    }

    /*
    * Method to get the Spinner's selected item index
     */
    private fun getSpinnerPosition(spinner: Spinner, category: String): Int {
        for (index in 0..spinner.count) {
            if (spinner.getItemAtPosition(index) == category)
                return index
        }
        return 0
    }

    /*
    * Method to initialize and setup the spinners
     */
    private fun setupSpinners() {
        if (context != null && root != null) {
            //Get the categories

            //Create an Adapter for the categories spinner
            val catAdapter = ArrayAdapter(
                context!!,
                android.R.layout.simple_spinner_item,
                SharedPrefsUtils.getCategories(context!!)
            )

            //Set the DropDownViewResource of the categories Adapter
            catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            //Set the categories spinner's adapter
            root!!.spinnerCat.adapter = catAdapter

            //Create an Adapter for the types spinner
            val typeAdapter = ArrayAdapter.createFromResource(
                context!!,
                R.array.pay_types,
                android.R.layout.simple_spinner_item
            )

            //Set the DropDownViewResource of the types Adapter
            typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            //Set the types spinner's adapter
            root!!.spinnerType.adapter = typeAdapter
        }
    }

    /*
    * Method to ensure input data validation, and to save them
     */
    private fun validateAndSaveData(isIncome: Boolean) {
        if (root != null && listener != null) {
            //Get the transaction's name, and ensure that it is not empty
            val name = root!!.inputName.editText?.text.toString()
            if (name.isEmpty()) {
                root!!.inputName.error = resources.getString(R.string.name_error)
                return
            } else {
                root!!.inputName.error = null
            }

            //Get the transaction's amount, and ensure that it is larger than 0
            var amount = root!!.inputAmount.editText?.text.toString().toFloatOrNull()
            if (amount == null && amount != 0f) {
                root!!.inputAmount.error = resources.getString(R.string.amount_error)
                return
            } else {
                root!!.inputAmount.error = null
            }

            //Get the transaction's category, and ensure that it is valid
            val category: String
            if (root!!.spinnerCat.selectedItemPosition == 0) {
                root!!.spinnerCat.setBackgroundResource(R.drawable.spinner_background_error)
                root!!.tvCatError.visibility = View.VISIBLE
                return
            } else {
                root!!.tvCatError.visibility = View.GONE
                root!!.spinnerCat.setBackgroundResource(R.drawable.spinner_background)
                category = root!!.spinnerCat.selectedItem.toString()
            }

            //Get the transaction's type, and ensure that it is valid
            val type: String
            if (root!!.spinnerType.selectedItemPosition == 0) {
                root!!.spinnerType.setBackgroundResource(R.drawable.spinner_background_error)
                root!!.tvTypeError.visibility = View.VISIBLE
                return
            } else {
                root!!.tvTypeError.visibility = View.GONE
                root!!.spinnerType.setBackgroundResource(R.drawable.spinner_background)
                type = root!!.spinnerType.selectedItem.toString()
            }

            //Set the Transaction's start and end time, if it is recurring
            if (!root!!.checkRecurring.isChecked) {
                startDate = DateUtils.getTimestamp(startDate, startHour, startMinute)
                endDate = startDate
            }

            //If the user's chooses this Transaction to be an Expense, multiply the amount by -1, to make it negative
            if (!isIncome) {
                amount *= -1
            }

            //Create a new Transaction object
            val transaction = Transaction(
                name,
                startDate,
                endDate,
                amount,
                category,
                type,
                root!!.inputComments.editText?.text.toString()
            )

            //Make the appropriate actions, whither Insert or Update operation,
            // according to wither there are passed arguments or not
            if (arguments != null && arguments!!.containsKey(ID_KEY)) {
                transaction.id = arguments!!.getInt(ID_KEY)
                ViewModelProvider(this).get(TransactionViewModel::class.java)
                    .updateTransaction(transaction)
                if (activity != null) {
                    activity!!.supportFragmentManager.popBackStackImmediate()
                }
            } else {
                ViewModelProvider(this).get(TransactionViewModel::class.java)
                    .insertTransaction(transaction)
                listener!!.onDataAdded()
            }
        }
    }

    /*
    * Method to set the time
     */
    fun setTime(hour: Int, minute: Int) {
        startHour = hour
        startMinute = minute
        tvTimeVal.text = DateFormat.getTimeInstance(DateFormat.SHORT)
            .format(Date(DateUtils.getTimestamp(startDate, startHour, startMinute)))
    }

    /*
    * Method to set the start date
     */
    fun setStartDate(year: Int, month: Int, day: Int) {
        startDate = DateUtils.getTimestamp(year, month, day)
        root!!.tvDateVal.text = DateFormat.getDateInstance().format(Date(startDate))
    }

    /*
    * Method to set the end date
     */
    fun setEndDate(year: Int, month: Int, day: Int) {
        endDate = DateUtils.getTimestamp(year, month, day)
        root!!.tvEndDate.text = DateFormat.getDateInstance().format(Date(endDate))
    }

    /**
     * Companion object, that allow to pass arguments to this Fragment, when editing a Transaction
     */
    companion object {
        const val ID_KEY = "com.asif.expensemanager.transaction_id"

        @JvmStatic
        fun newInstance(id: Int) = AddEditFragment().apply {
            arguments = Bundle().apply {
                putInt(ID_KEY, id)
            }
        }
    }
}