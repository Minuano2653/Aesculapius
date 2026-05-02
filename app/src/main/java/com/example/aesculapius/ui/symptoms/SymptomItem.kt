package com.example.aesculapius.ui.symptoms

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "symptoms")
data class SymptomItem(
    @PrimaryKey val id: String,
    val name: String,
    val createdAt: String
)
