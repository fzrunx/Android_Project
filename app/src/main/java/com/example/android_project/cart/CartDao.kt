package com.example.android_project.cart

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: CartRoom)

    @Query("SELECT * FROM cart")
    fun getAllCartItems(): Flow<List<CartRoom>>

    @Delete
    suspend fun delete(item: CartRoom)

    @Query("UPDATE cart SET quantity = :quantity WHERE id = :id")
    suspend fun updateQuantity(id: Int, quantity: Int)

    @Query("UPDATE cart SET isSelected = :selected WHERE id = :itemId")
    suspend fun updateIsSelected(itemId: Int, selected: Boolean)
    @Query("UPDATE cart SET isSelected = :selected")
    suspend fun updateAllSelected(selected: Boolean)
    @Query("SELECT * FROM cart")
    suspend fun getAllCartItemsOnce(): List<CartRoom>
    @Query("SELECT * FROM cart WHERE productId = :productId LIMIT 1")
    suspend fun getCartItemByProductId(productId: String): CartRoom?
}