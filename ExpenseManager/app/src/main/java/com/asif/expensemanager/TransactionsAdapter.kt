package com.asif.expensemanager

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.asif.expensemanager.db.Transaction
import kotlinx.android.synthetic.main.transaction_layout.view.*
import java.util.*

class TransactionsAdapter : RecyclerView.Adapter<TransactionsAdapter.TransactionsHolder>() {
    interface TransactionsAdapterListener {
        fun onTransactionClick(transaction: Transaction)
        fun onTransactionLongClick(transaction: Transaction)
    }

    private var items: List<Transaction>? = null
    private val c: Calendar = Calendar.getInstance()
    private var listener: TransactionsAdapterListener? = null

    fun updateItems(items: List<Transaction>?) {
        if(items != null) {
            this.items = items
            notifyDataSetChanged()
        }
    }

    class TransactionsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.tvName
        val dateTextView: TextView = itemView.tvDate
        val amountTextView: TextView = itemView.tvAmount
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionsHolder {
        if (parent.context is TransactionsAdapterListener) {
            listener = parent.context as TransactionsAdapterListener
        }
        return TransactionsHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.transaction_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: TransactionsHolder, position: Int) {
        if (items != null) {
            val item = items!![position]
            val today = Utils.getCurrentTimestamp()
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
            holder.nameTextView.text = item.name
            holder.dateTextView.text =
                "${c[Calendar.DAY_OF_MONTH]}/${c[Calendar.MONTH] + 1}/${c[Calendar.YEAR]}"
            holder.amountTextView.text = item.amount.toString()
            if (item.amount >= 0) {
                holder.amountTextView.setTextColor(Color.GREEN)
            } else {
                holder.amountTextView.setTextColor(Color.RED)
            }
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

    override fun getItemCount(): Int {
        return if (items != null) {
            items!!.size
        } else {
            0
        }
    }
}