package com.asif.expensemanager.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.asif.expensemanager.R
import com.asif.expensemanager.background.db.Transaction
import com.asif.expensemanager.background.utils.DateUtils
import kotlinx.android.synthetic.main.header_layout.view.*
import kotlinx.android.synthetic.main.transaction_layout.view.*
import java.text.DateFormat.getDateInstance
import java.util.*
import kotlin.math.roundToInt

/**
 * Adapter for transactions RecyclerView
 */
class TransactionsAdapter(val context: Context?, private val needHeader: Boolean = false) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    /**
     * Adapter's actions listener
     */
    interface TransactionsAdapterListener {
        fun onTransactionClick(transaction: Transaction)
        fun onTransactionLongClick(transaction: Transaction)
    }

    //Transactions list
    private var items: List<Transaction>? = null

    //Net balance & total expenses
    private var balance = 0f
    private var expenses = 0f

    //Instance of the Calendar
    private val c: Calendar = Calendar.getInstance()

    //Instance of the listener
    private var listener: TransactionsAdapterListener? = null

    /*
    * Method to update items
     */
    fun setItems(items: List<Transaction>?) {
        if (items != null) {
            this.items = items
            notifyDataSetChanged()
        }
    }

    /*
    * Method to update the balance
     */
    fun setBalance(balance: Float) {
        this.balance = balance
        notifyDataSetChanged()
    }

    /*
    * Method to update the expenses
     */
    fun setExpenses(expenses: Float) {
        this.expenses = expenses
        notifyDataSetChanged()
    }

    /*
    * Overridden Method, used to get the item's view type, according to it position,
    *  if it is the first item and the RecyclerView needs a header, specify the type to Header,
    *  else; specify the type to Transaction
     */
    override fun getItemViewType(position: Int): Int {
        return if (needHeader && position == 0) {
            HEADER_VIEW_TYPE
        } else {
            TRANSACTION_VIEW_TYPE
        }
    }

    /*
    * Overridden Method used to create the Holder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        //Initialize the listener
        if (parent.context is TransactionsAdapterListener) {
            listener = parent.context as TransactionsAdapterListener
        }

        // Inflate the appropriate layout, and create the appropriate holder, according to the view type
        return if (viewType == HEADER_VIEW_TYPE) {
            HeaderHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.header_layout, parent, false)
            )
        } else {
            TransactionsHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.transaction_layout, parent, false)
            )
        }
    }

    /*
    * Overridden Method used to bind the holder
     */
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        //If the holder is a HeaderHolder, show the appropriate data on the header,
        // else; do required operations to show the transaction's data
        if (holder is HeaderHolder) {
            holder.balanceTextView.text = "$$balance"

            val incomes = balance + expenses
            holder.incomeTextView.text = "$$incomes"
            holder.expensesTextView.text = "$$expenses"

            holder.balanceProgress.max = (incomes + expenses).roundToInt()
            holder.balanceProgress.progress = incomes.roundToInt()
            holder.balanceProgress.secondaryProgress = holder.balanceProgress.max

        } else if (items != null && holder is TransactionsHolder) {
            //Get the item
            val item = if (needHeader) {
                items!![position - 1]
            } else {
                items!![position]
            }

            //Get the current time in millis
            val today = DateUtils.getCurrentTimestamp()

            //If current time exceeded the item's end data, show the end data,
            // else if current time is less than the item's start date, show the start data,
            // In other cases show current time
            when {
                today > item.endDate -> {
                    c.timeInMillis = item.endDate
                }
                today < item.startDate -> {
                    c.timeInMillis = item.startDate
                }
                else -> {
                    c.timeInMillis = today
                }
            }

            //Show transaction's name, data & amount
            holder.nameTextView.text = item.name
            holder.dateTextView.text = getDateInstance().format(Date(c.timeInMillis))
            holder.amountTextView.text = item.amount.toString()

            //Change the amount text color, according to it value
            if (item.amount >= 0) {
                holder.amountTextView.setTextColor(Color.GREEN)
            } else {
                holder.amountTextView.setTextColor(Color.RED)
            }

            //set the appropriate background to Type View, according to item's type
            if (context != null) {
                holder.typeView.setBackgroundResource(
                    when (item.type) {
                        context.resources.getString(R.string.cash) -> R.color.colorCash
                        context.resources.getString(R.string.credit) -> R.color.colorCredit
                        context.resources.getString(R.string.cheque) -> R.color.colorCheque
                        context.resources.getString(R.string.other_pays) -> R.color.colorOtherPays
                        else -> 0
                    }
                )
            }

            //Set click & long click events listeners on the transaction view, if no header is present.
            // Because the header is only shown on MonthYearFragment, which gets static data and doesn't update the data
            // automatically when it changes. So to prevent the fragment from showing out-dated data,
            // don't let the user update the transactions when the fragment is shown, i.e. the header is shown,
            // by not setting any event listeners on the items. This approach is used for performance reasons.
            if (!needHeader) {
                holder.itemView.setOnClickListener {
                    if (listener != null) {
                        listener!!.onTransactionClick(item)
                    }
                }
                holder.itemView.setOnLongClickListener {
                    if (listener != null) {
                        listener!!.onTransactionLongClick(item)
                        true
                    } else {
                        false
                    }
                }
            }
        }
    }

    /*
    * Overridden Method used to get the total numbers of items to be shown
     */
    override fun getItemCount(): Int {
        return if (items != null) {
            if (needHeader)
            //If there is a header, the item's count equals the transactions number + 1 (The header)
                items!!.size + 1
            else
            //If no header is present, return the transactions number
                items!!.size
        } else {
            //Return 0 if the list is null
            0
        }
    }

    /**
     * Companion object for View Type Constant values
     */
    companion object {
        const val TRANSACTION_VIEW_TYPE = 0
        const val HEADER_VIEW_TYPE = 1
    }

    /**
     * Holder for Transactions items
     */
    class TransactionsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val typeView: View = itemView.typeView
        val nameTextView: TextView = itemView.tvName
        val dateTextView: TextView = itemView.tvDate
        val amountTextView: TextView = itemView.tvAmount
    }

    /**
     * Holder for the header
     */
    class HeaderHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val incomeTextView: TextView = itemView.tvIncomeVal
        val expensesTextView: TextView = itemView.tvExpensesVal
        val balanceTextView: TextView = itemView.tvBalanceVal
        val balanceProgress: ProgressBar = itemView.progressBalance
    }

}