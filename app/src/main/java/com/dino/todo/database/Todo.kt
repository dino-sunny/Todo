package com.dino.todo.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.*

@Entity(tableName = "todo_table")
data class Todo(
        @PrimaryKey(autoGenerate = true)
        var todoId: Long = 0L,
        @ColumnInfo(name = "title")
        var title: String = "",
        @ColumnInfo(name = "description")
        var description: String = "",
        var date: Long = 0L,
        var isNotification: Boolean = false,
        var completed: Boolean = false
){
        fun getDateTime(): String? {
                return if (date>0) {
                        try {
                                val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                                val netDate = Date(date)
                                sdf.format(netDate)
                        } catch (e: Exception) {
                                e.toString()
                        }
                }else{
                        ""
                }
        }
        fun getTime(): String? {
                return if (date>0) {
                        try {
                                val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
                                val netDate = Date(date)
                                sdf.format(netDate)
                        } catch (e: Exception) {
                                e.toString()
                        }
                }else{
                        ""
                }
        }
}
