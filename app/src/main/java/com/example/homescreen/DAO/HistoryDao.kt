package com.example.homescreen.DAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.homescreen.entity.HistoryEntity

@Dao
interface HistoryDao {

    @Insert
    suspend fun insert(history: HistoryEntity)

    @Query("SELECT * FROM history ORDER BY id DESC")
    suspend fun getAll(): List<HistoryEntity>

    @Query("DELETE FROM history")
    suspend fun clearAll()
}