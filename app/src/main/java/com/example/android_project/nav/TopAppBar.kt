package com.example.android_project.nav


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.android_project.user.info.login.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(navController: NavController, isLoggedIn: Boolean, userId: String?, viewModel: LoginViewModel) {
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    // 홈 화면(네비게이션 시작 화면)에서는 뒤로가기 버튼 숨김
    val showBackButton = currentRoute != "main_Screen" && currentRoute != "login"

    TopAppBar(
        title = { Text("쇼핑몰") },
        navigationIcon = {
            if (showBackButton) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "뒤로가기")
                }
            }
        },
        actions = {
            if (isLoggedIn && userId != null) {
                // 로그인 상태 → 유저 아이디 + 로그아웃 버튼
                Text(
                    text = "반갑습니다. $userId 님",
                    color = Color.Black,
                    modifier = Modifier.padding(end = 8.dp)
                )
                IconButton(
                    onClick = {
                        viewModel.logout()
                        // 로그아웃 후 로그인 화면으로 이동 (옵션)
                        navController.navigate("login") {
                            popUpTo("main_Screen") { inclusive = true }
                        }
                    },
                    modifier = Modifier.background(Color.Gray)
                ) {
                    Text("로그아웃", color = Color.White)
                }
            } else {
                // 로그아웃 상태 → 로그인 버튼
                IconButton(
                    onClick = {
                        navController.navigate("login")
                    },
                    modifier = Modifier.background(Color.Gray)
                ) {
                    Text("로그인", color = Color.White)
                }
            }
        }
    )
}