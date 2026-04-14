package com.example.aesculapius.ui.tests

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "recommendation_items")
data class RecommendationItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val score: Int,
    val date: LocalDate
)
