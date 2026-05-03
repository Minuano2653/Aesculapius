package com.example.aesculapius.ui.diary

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "diary_entries")
data class DiaryEntryItem(
    @PrimaryKey val date: LocalDate,
    val symptomsJson: String,
    val note: String,
    val createdAt: Long
)
