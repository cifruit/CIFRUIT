package com.gazura.projectcapstone.data.history
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history_table")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true) val id:Int=0,
    val email: String,
    val imageUri: String,
    val predictedClass: String,
    val confidence: String,
    val recommendation: String,
    val tanggal: String
)

