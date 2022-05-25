package com.ecs198f.foodtrucks

import androidx.room.Entity

@Entity
data class FoodReviewPost(
    val content: String,
    val imageUrls: List<String>
)
