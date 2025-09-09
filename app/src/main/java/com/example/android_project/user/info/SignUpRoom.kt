package com.example.android_project.user.info

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "signUp", indices = [Index(value = ["userid"], unique = true)])
data class SignUpRoom (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userid: String,
    val userpassword: String,
    val username: String,
    val useremail: String,
    val userphone: String,
    val useraddress: String,
    val registerDate: Long = System.currentTimeMillis() // ✅ 등록일 (밀리초)
)
