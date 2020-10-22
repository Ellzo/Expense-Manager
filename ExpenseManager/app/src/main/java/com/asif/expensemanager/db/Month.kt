package com.asif.expensemanager.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "months_table")
data class Month(
    @PrimaryKey
    val id: Int,
    var amountSaved: Int,
    var amountSpent: Int,
    var transactions: LinkedList<Transaction>
)