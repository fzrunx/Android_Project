package com.example.android_project.user.info

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_project.user.info.SignUpDB
import com.example.android_project.user.info.SignUpRoom
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SignUpViewModel(application: Application) : AndroidViewModel(application) {
    private val signUpDao = SignUpDB.getInstance(application).signUpDao()
    private val _signup = MutableStateFlow<List<SignUpRoom>>(emptyList())
    val signup: StateFlow<List<SignUpRoom>> = _signup
    private val _checkedItems = MutableStateFlow<Set<SignUpRoom>>(emptySet())
    val checkedItems: StateFlow<Set<SignUpRoom>> = _checkedItems

    init {
        viewModelScope.launch {
            signUpDao.getAllUsers().collect { users ->
                _signup.value = users
            }
        }
    }

    fun addUser(user: SignUpRoom, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            // DB에 동일한 userid가 있는지 먼저 체크
            val exists = signUpDao.getUserByUserId(user.userid) != null
            if (exists) {
                onResult(false) // 이미 존재하는 ID
            } else {
                try {
                    signUpDao.insert(user)
                    onResult(true) // 성공적으로 추가됨
                } catch (e: Exception) {
                    onResult(false)
                }
            }
        }
    }
}
