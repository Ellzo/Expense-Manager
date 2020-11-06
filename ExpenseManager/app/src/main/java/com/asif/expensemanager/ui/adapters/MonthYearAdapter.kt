package com.asif.expensemanager.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.asif.expensemanager.R
import com.asif.expensemanager.background.db.Transaction
import com.asif.expensemanager.background.utils.DateUtils
import com.asif.expensemanager.background.utils.SharedPrefsUtils
import com.google.android.material.card.MaterialCardView
import kotlinx.android.synthetic.main.card_month_year.view.*
import kotlin.math.roundToInt

/**
 * Adapter for Month/Year cards
 */
class MonthYearAdapter(val context: Context) :
    RecyclerView.Adapter<MonthYearAdapter.MonthYearHolder>() {
    /**
     * Listener for this Adapter
     */
    interface MonthYearListener {
        fun onCardClick(
            monthYearName: String,
            balance: Float,
            expenses: Float,
            transactions: ArrayList<Transaction>
        )
    }

    //String ArrayList, to hold months/years names
    private var monthsYears: ArrayList<String>? = null

    //ArrayList to hold the transactions of each month/year
    private var transactions: ArrayList<ArrayList<Transaction>>? = null

    //Boolean indicating whither to put the Monthly Budget under consideration or not,
    // In general, if the cards shown are monthly cards than consider the budget,
    // if they are annually cards, no need to consider the monthly budget
    private var considerBudget = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonthYearHolder {
        //Inflate the layout and create the View Holder
        return MonthYearHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.card_month_year, parent, false)
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MonthYearHolder, position: Int) {
        if (transactions != null && monthsYears != null) {
            //Get the month/year name
            val monthYear = monthsYears!![position]

            //Show the month/year name
            holder.monthYearTextView.text = monthYear

            //Get the transactions of the month/year
            val monthYearTransactions = transactions!![position]

            //Variable to hold the net balance of the month/year, it gets 0 initially
            var balance = 0f

            //Variable to hold the total expenses of the month/year, it gets 0 initially
            var expenses = 0f

            //Set the jump size to a single day
            val stepSize = 1000 * 60 * 60 * 24L

            //Iterate through the transactions of the month/year
            for (transaction in monthYearTransactions) {
                //For each  transaction, add its amount to the balance for each day between the start date & end date,
                // Break the loop however when the current day is exceeded, to not consider transactions that didn't happen yet
                for (i in transaction.startDate..transaction.endDate step stepSize) {
                    if (i <= DateUtils.getCurrentTimestamp()) {
                        if (!considerBudget || DateUtils.getMonthName(context, i) == monthYear) {
                            balance += transaction.amount
                            if (transaction.amount < 0) {
                                expenses += -1 * transaction.amount
                            }
                        }
                    } else {
                        break
                    }
                }
            }

            //Set the balance text
            holder.balanceTextView.text = "$$balance"

            //Change the balance text color, and the state view background, according to the value
            when {
                balance < 0 -> {
                    holder.balanceTextView.setTextColor(Color.RED)
                    holder.stateView.setBackgroundColor(Color.RED)
                }
                balance > 0 -> {
                    holder.balanceTextView.setTextColor(Color.GREEN)
                }
                else -> {
                    holder.balanceTextView.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.colorPrimary
                        )
                    )
                }
            }

            //The incomes equals the net balance plus the total expenses
            val incomes = balance + expenses

            //Set the progress bar attributes
            holder.balanceProgress.max = (incomes + expenses).roundToInt()
            holder.balanceProgress.progress = incomes.roundToInt()
            holder.balanceProgress.secondaryProgress = holder.balanceProgress.max

            //Show the Warning if the budget is considered, and the expenses exceeded the budget
            if (considerBudget
                && SharedPrefsUtils.getBudget(context) > 0
                && expenses > SharedPrefsUtils.getBudget(context)
            ) {
                holder.stateView.setBackgroundColor(Color.RED)
                holder.warningImg.visibility = View.VISIBLE
                holder.warningTextView.visibility = View.VISIBLE
            } else {
                holder.warningImg.visibility = View.GONE
                holder.warningTextView.visibility = View.GONE
            }

            //Set click listener on the card(parent view)
            holder.parent.setOnClickListener {
                if (context is MonthYearListener) {
                    context.onCardClick(monthYear, balance, expenses, monthYearTransactions)
                }
            }
        }
    }


    override fun getItemCount(): Int {
        //Return the number of months/years
        return if (monthsYears != null)
            monthsYears!!.size
        else
            0
    }

    /*
    * Method to set Adapter's data
     */
    fun setData(
        monthsYears: ArrayList<String>,
        transactions: ArrayList<ArrayList<Transaction>>,
        considerBudget: Boolean
    ) {
        this.monthsYears = monthsYears
        this.transactions = transactions
        this.considerBudget = considerBudget
        notifyDataSetChanged()
    }

    /**
     * Holder for the month/year
     */
    class MonthYearHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val parent: MaterialCardView = itemView.card
        val stateView: View = itemView.viewState
        val monthYearTextView: TextView = itemView.tvMonthYear
        val balanceTextView: TextView = itemView.tvBalanceVal
        val balanceProgress: ProgressBar = itemView.progressBalance
        val warningImg: ImageView = itemView.imgWarning
        val warningTextView: TextView = itemView.tvWarning
    }
}