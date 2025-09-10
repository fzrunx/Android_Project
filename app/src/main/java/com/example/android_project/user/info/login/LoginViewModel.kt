package com.example.android_project.user.info.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.android_project.user.info.SignUpRoom
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val context: Context, private val repository: LoginRepository) : ViewModel() {

    private val prefs = context.getSharedPreferences("login_prefs", Context.MODE_PRIVATE)

    private val _loginState = MutableStateFlow(false)
    val loginState: StateFlow<Boolean> get() = _loginState
    private val _currentUserId = MutableStateFlow<String?>(null)
    val currentUserId: StateFlow<String?> get() = _currentUserId
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> get() = _isLoading


    /** 로그인 */
    fun login(userId: String, password: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val user = repository.login(userId, password)
            if (user != null) {
                _loginState.value = true
                // SharedPreferences에 로그인 상태 저장
                _currentUserId.value = user.userid
                prefs.edit().putString("current_user_id", user.userid).apply()
                onResult(true)
            } else {
                _loginState.value = false
                _currentUserId.value = null
                onResult(false)
            }
        }
    }

    /** 로그아웃 */
    fun logout() {
        _loginState.value = false
        _currentUserId.value = null
        prefs.edit().remove("current_user_id").apply()
    }

    /** 자동 로그인 체크 */
    fun checkAutoLogin() {
        val savedId = prefs.getString("current_user_id", null)
        _loginState.value = savedId != null
        _currentUserId.value = savedId
        _isLoading.value = false // 로딩 완료
    }
}