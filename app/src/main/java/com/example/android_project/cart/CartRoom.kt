package com.example.android_project.cart

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "cart", indices = [Index(value = ["id"], unique = true)])
data class CartRoom(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val productId: String,
    val image: String,
    val link: String,
    val keyword: String
)
