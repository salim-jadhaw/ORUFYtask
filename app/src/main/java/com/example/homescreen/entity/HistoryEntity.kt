package com.example.homescreen.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val url: String,
    val time: String
) {
    // Secondary constructor for easy creation
    constructor(url: String, time: String) : this(0, url, time)
}