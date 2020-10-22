package com.asif.expensemanager

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.asif.expensemanager.db.Transaction
import com.google.android.material.card.MaterialCardView
import kotlinx.android.synthetic.main.card_month_year.view.*

class MonthYearAdapter(val context: Context) :
    RecyclerView.Adapter<MonthYearAdapter.MonthYearHolder>() {
    interface MonthYearListener {
        fun onCardClick(balance: Float, expenses: Float, transactions: ArrayList<Transaction>)
    }

    private var monthsYears: ArrayList<String>? = null
    private var transactions: ArrayList<ArrayList<Transaction>>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonthYearHolder {
        return MonthYearHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.card_month_year, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MonthYearHolder, position: Int) {
        if (transactions != null && monthsYears != null) {
            holder.monthYearTextView.text = monthsYears!![position]
            val monthYearTransactions = transactions!![position]
            var balance = 0f
            var expenses = 0f
            val stepSize = 1000 * 60 * 60 * 24L
            for (transaction in monthYearTransactions) {
                // TODO: 10/21/20 Add stepsize or interval attribute to transaction class
                for (i in transaction.startDate..transaction.endDate step stepSize) {
                    if(i <= Utils.getCurrentTimestamp()) {
                        balance += transaction.amount
                        if (transaction.amount < 0) {
                            expenses += -1 * transaction.amount
                        }
                    }else{
                        break
                    }
                }
            }
            holder.balanceTextView.text = balance.toString()
            if (expenses > Utils.getBudget(context)) {
                holder.warningImg.visibility = View.VISIBLE
                holder.warningTextView.visibility = View.VISIBLE
            } else {
                holder.warningImg.visibility = View.GONE
                holder.warningTextView.visibility = View.GONE
            }

            holder.parent.setOnClickListener {
                if (context is MonthYearListener) {
                    context.onCardClick(balance, expenses, monthYearTransactions)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return if (monthsYears != null)
            monthsYears!!.size
        else
            0
    }

    fun setData(monthsYears: ArrayList<String>, transactions: ArrayList<ArrayList<Transaction>>) {
        this.monthsYears = monthsYears
        this.transactions = transactions
        notifyDataSetChanged()
    }

    class MonthYearHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val parent: MaterialCardView = itemView.card
        val monthYearTextView: TextView = itemView.tvMonthYear
        val warningImg: ImageView = itemView.imgWarning
        val warningTextView: TextView = itemView.tvWarning
        val balanceTextView: TextView = itemView.tvBalanceVal
    }
}