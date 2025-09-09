package com.example.android_project.cart

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [CartRoom::class], version = 1, exportSchema = false)
abstract class CartDB: RoomDatabase() {
    abstract fun cartDao(): CartDao

    companion object {
        @Volatile
        private var INSTANCE: CartDB? = null

        fun getDatabase(context: Context): CartDB {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CartDB::class.java,
                    "shopping_db" // DB 파일명
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }

}
