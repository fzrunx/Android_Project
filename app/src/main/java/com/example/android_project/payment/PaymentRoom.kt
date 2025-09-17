package com.example.android_project.payment

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "payment_history")
data class PaymentRoom(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String,           // 누가 결제했는지
    val productId: String,
    val title: String,
    val image: String,
    val link: String,
    val quantity: Int,
    val lprice: String,
    val keyword: String,
    val timestamp: Long = System.currentTimeMillis(), // 결제 시간
    val paymentGroupId: String
)
