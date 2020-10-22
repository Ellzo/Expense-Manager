package com.asif.expensemanager.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

class Converters {
    companion object {
        @TypeConverter
        @JvmStatic
        fun stringToLinkedList(value: String): LinkedList<Transaction> {
            val listType = object : TypeToken<LinkedList<String>>() {}.type
            return Gson().fromJson(value, listType)
        }

        @TypeConverter
        @JvmStatic
        fun linkedListToString(value: LinkedList<Transaction>): String = Gson().toJson(value)
    }
}