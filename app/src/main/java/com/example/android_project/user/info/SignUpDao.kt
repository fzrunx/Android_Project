package com.example.android_project.user.info

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SignUpDao {
    @Query("SELECT * FROM signUP WHERE userid = :userid LIMIT 1")
    suspend fun getUserByUserId(userid: String): SignUpRoom?

    @Query("SELECT * FROM signUP")
    fun getAllUsers(): Flow<List<SignUpRoom>>
    @Insert
    suspend fun insert(user: SignUpRoom)

    @Delete
    suspend fun deleteUser(user: SignUpRoom)
    @Delete
    suspend fun deleteUsers(users: List<SignUpRoom>)

    @Update
    suspend fun update(user: SignUpRoom)
}