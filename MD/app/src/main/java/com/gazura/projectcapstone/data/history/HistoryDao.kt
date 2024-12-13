package com.gazura.projectcapstone.data.history

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: HistoryEntity)

    @Query("SELECT * FROM history_table WHERE email = :email")
    fun getHistoriesByEmail(email: String): Flow<List<HistoryEntity>>

    @Query("DELETE FROM history_table WHERE id = :id")
    suspend fun deleteHistoryById(id: Int)
}