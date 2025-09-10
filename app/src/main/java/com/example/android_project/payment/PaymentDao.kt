package com.example.android_project.payment

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentDao {
    @Insert
    suspend fun insertPayment(item: PaymentRoom)

    @Query("SELECT * FROM payment_history WHERE userId = :userId ORDER BY timestamp DESC")
    fun getPaymentsByUser(userId: String): Flow<List<PaymentRoom>>
}