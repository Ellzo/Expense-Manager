package com.asif.expensemanager

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
import com.asif.expensemanager.db.Transaction
import com.asif.expensemanager.db.TransactionViewModel
import kotlinx.android.synthetic.main.fragment_add_edit.*
import kotlinx.android.synthetic.main.fragment_add_edit.view.*
import java.util.*

class AddEditFragment : Fragment() {
    interface AddEditListener {
        fun onDataAdded()
    }

    private var startMinute = 0
    private var startHour = 0
    private var startDate: Long = 0
    private var endDate: Long = 0
    private var root: View? = null
    private var listener: AddEditListener? = null

    override fun onAttach(context: Context) {
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

        setupSpinner()
        root!!.toolbar.setTitleTextColor(Color.WHITE)
        root!!.toolbar.setNavigationIcon(R.drawable.ic_clear)
        root!!.toolbar.setNavigationOnClickListener {
            if (activity != null) {
                activity!!.supportFragmentManager.popBackStackImmediate()
            }
        }

        val calendar = Calendar.getInstance()

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
                    calendar.timeInMillis = endDate
                    root!!.tvEndDate.text =
                        "${calendar[Calendar.DAY_OF_MONTH]}/${calendar[Calendar.MONTH] + 1}/${calendar[Calendar.YEAR]}"
                    calendar.timeInMillis = startDate
                    startHour = calendar[Calendar.HOUR_OF_DAY]
                    startMinute = calendar[Calendar.MINUTE]
                    root!!.tvDateVal.text =
                        "${calendar[Calendar.DAY_OF_MONTH]}/${calendar[Calendar.MONTH] + 1}/${calendar[Calendar.YEAR]}"
                    root!!.tvTimeVal.text =
                        "${calendar[Calendar.HOUR_OF_DAY]}:${calendar[Calendar.MINUTE]}"
                    if (startDate != endDate) {
                        root!!.checkRecurring.isChecked = true
                    }
                })
        } else {
            root!!.toolbar.title = getString(R.string.add_tran)
            startDate = Utils.getCurrentTimestamp()
            calendar.timeInMillis = startDate
            startHour = calendar[Calendar.HOUR_OF_DAY]
            startMinute = calendar[Calendar.MINUTE]
            endDate = startDate
            val initialDate =
                "${calendar[Calendar.DAY_OF_MONTH]}/${calendar[Calendar.MONTH] + 1}/${calendar[Calendar.YEAR]}"
            root!!.tvDateVal.text = initialDate
            root!!.tvEndDate.text = initialDate
            root!!.tvTimeVal.text = "${calendar[Calendar.HOUR_OF_DAY]}:${calendar[Calendar.MINUTE]}"
        }

        root!!.btnIncome.setOnClickListener {
            validateAndSaveData(true)
        }

        root!!.btnExpense.setOnClickListener {
            validateAndSaveData(false)
        }

        root!!.linearDate.setOnClickListener {
            calendar.timeInMillis = startDate
            DatePicker.newInstance(
                true,
                calendar[Calendar.YEAR],
                calendar[Calendar.MONTH],
                calendar[Calendar.DAY_OF_MONTH]
            ).show(childFragmentManager, "datePicker")
        }

        root!!.linearTime.setOnClickListener {
            TimePicker.newInstance(startHour, startMinute).show(childFragmentManager, "timePicker")
        }

        root!!.linearEndDate.setOnClickListener {
            calendar.timeInMillis = endDate
            DatePicker.newInstance(
                false,
                calendar[Calendar.YEAR],
                calendar[Calendar.MONTH],
                calendar[Calendar.DAY_OF_MONTH]
            ).show(childFragmentManager, "datePicker")
        }

        return root
    }

    private fun getSpinnerPosition(spinner: Spinner, category: String): Int {
        for (index in 0..spinner.count) {
            if (spinner.getItemAtPosition(index) == category)
                return index
        }
        return 0
    }

    private fun setupSpinner() {
        if (context != null && root != null) {
            val catAdapter = ArrayAdapter.createFromResource(
                context!!,
                R.array.categories,
                android.R.layout.simple_spinner_item
            )
            catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            root!!.spinnerCat.adapter = catAdapter

            val typeAdapter = ArrayAdapter.createFromResource(
                context!!,
                R.array.types,
                android.R.layout.simple_spinner_item
            )
            typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            root!!.spinnerType.adapter = typeAdapter
        }
    }

    private fun validateAndSaveData(isIncome: Boolean) {
        if (root != null && listener != null) {
            val name = root!!.inputName.editText?.text.toString()
            if (name.isEmpty()) {
                root!!.inputName.error = resources.getString(R.string.name_error)
                return
            } else {
                root!!.inputName.error = null
            }

            var amount = root!!.inputAmount.editText?.text.toString().toFloatOrNull()
            if (amount == null && amount != 0f) {
                root!!.inputAmount.error = resources.getString(R.string.amount_error)
                return
            } else {
                root!!.inputAmount.error = null
            }

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

            if (!root!!.checkRecurring.isChecked) {
                startDate = Utils.getTimestamp(startDate, startHour, startMinute)
                endDate = startDate
            }

            if (!isIncome) {
                amount *= -1
            }

            val transaction = Transaction(
                name,
                startDate,
                endDate,
                amount,
                category,
                type,
                root!!.inputComments.editText?.text.toString()
            )

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

    fun setTime(hour: Int, minute: Int) {
        //TODO: create up button
        startHour = hour
        startMinute = minute
        tvTimeVal.text = "${hour}:${minute}"
    }

    fun setStartDate(year: Int, month: Int, day: Int) {
        startDate = Utils.getTimestamp(year, month, day)
        val date = "${day}/${month + 1}/${year}"
        root!!.tvDateVal.text = date
    }

    fun setEndDate(year: Int, month: Int, day: Int) {
        endDate = Utils.getTimestamp(year, month, day)
        root!!.tvEndDate.text = "${day}/${month + 1}/${year}"
    }

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