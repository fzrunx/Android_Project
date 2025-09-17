package com.example.android_project.payment

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [PaymentRoom::class], version = 1, exportSchema = false)
abstract class PaymentDB : RoomDatabase() {

    abstract fun paymentDao(): PaymentDao

    companion object {
        @Volatile
        private var INSTANCE: PaymentDB? = null

        fun getDatabase(context: Context): PaymentDB {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PaymentDB::class.java,
                    "payment_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}