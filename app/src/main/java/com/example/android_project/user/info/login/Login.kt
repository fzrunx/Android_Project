package com.example.android_project.user.info.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.android_project.user.info.SignUpDB

@Composable
fun LoginScreen(navController: NavController, modifier: Modifier = Modifier, viewModel: LoginViewModel) {
    var userId by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loginError by remember { mutableStateOf(false) }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(25.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "로그인",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(25.dp))

        // 아이디 입력창
        TextField(
            value = userId,
            onValueChange = { userId= it },
            label = { Text("아이디") },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 비밀번호 입력창
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("비밀번호") },
            visualTransformation = PasswordVisualTransformation(), // 입력 숨기기
            singleLine = true
        )

        Spacer(modifier = Modifier.height(25.dp))
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row {
                // 로그인 버튼
                Button(onClick = {
                    viewModel.login(userId, password) { success ->
                        if (success) {
                            loginError = false
                            navController.navigate("main_Screen")
                        } else {
                            loginError = true
                        }
                    }
                }) {
                    Text("로그인")
                }

                Spacer(modifier = Modifier.width(8.dp))

                // 회원가입 버튼
                Button(onClick = { navController.navigate("sign_Up") }) {
                    Text("회원가입")
                }
            }

            // 로그인 실패 문구
            if (loginError) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "아이디 또는 비밀번호가 올바르지 않습니다.",
                    color = Color.Red
                )
            }
        }

    }
}