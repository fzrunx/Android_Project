package com.example.android_project.user.info.login

import com.example.android_project.user.info.SignUpDao
import com.example.android_project.user.info.SignUpRoom


class LoginRepository (private val userDao: SignUpDao) {
    suspend fun login(id: String, pw: String): SignUpRoom? {
        val user = userDao.getUserByUserId(id)
        return if (user != null && user.userpassword == pw) user else null
    }
}