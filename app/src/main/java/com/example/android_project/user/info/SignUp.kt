package com.example.android_project.user.info

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController


@Composable
fun SignUp(navController: NavController, modifier: Modifier = Modifier,viewModel: SignUpViewModel = viewModel()) {
    var userid by remember { mutableStateOf("") }
    var userpassword by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var useremail by remember { mutableStateOf("") }
    var userphone by remember { mutableStateOf("") }
    var useraddress by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    // 입력 누락 다이얼로그 상태
    var showInputDialog by remember { mutableStateOf(false) }
    // 중복 ID 다이얼로그 상태
    var showDuplicateDialog by remember { mutableStateOf(false) }

    Column(modifier = modifier
        .fillMaxSize()
        .systemBarsPadding(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally)
    {
        Text(
            "회원가입",
            fontSize = 20.sp,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(20.dp)
        )
        Column() {
            OutlinedTextField(
                value = userid,
                onValueChange = { if (it.length <= 20) {
                    userid = it }
                },
                modifier = modifier
                    .padding(10.dp)
                    .fillMaxWidth(),
                placeholder = {Text("ID를 입력하세요")}
            )
            OutlinedTextField(
                value = userpassword,
                onValueChange = { if (it.length <= 20) {
                    userpassword = it }
                },
                modifier = modifier
                    .padding(10.dp)
                    .fillMaxWidth(),
                placeholder = {Text("비밀번호를 입력하세요")}
            )
            OutlinedTextField(
                value = username,
                onValueChange = { if (it.length <= 10) {
                    username = it}
                },
                modifier = modifier
                    .padding(10.dp)
                    .fillMaxWidth(),
                placeholder = {Text("이름을 입력하세요")}
            )
            OutlinedTextField(
                value = useremail,
                onValueChange = { useremail = it},
                modifier = modifier
                    .padding(10.dp)
                    .fillMaxWidth(),
                placeholder = {Text("email을 입력하세요")}
            )
            OutlinedTextField(
                value = userphone,
                onValueChange = { userphone = it},
                modifier = modifier
                    .padding(10.dp)
                    .fillMaxWidth(),
                placeholder = {Text("전화번호를 입력하세요")}
            )
            OutlinedTextField(
                value = useraddress,
                onValueChange = { useraddress = it},
                modifier = modifier
                    .padding(10.dp)
                    .fillMaxWidth(),
                placeholder = {Text("주소를 입력하세요")}
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Button(onClick = {
                if (userid.isNotBlank() && userpassword.isNotBlank() && username.isNotBlank() && useremail.isNotBlank()
                    && userphone.isNotBlank() && useraddress.isNotBlank()) {

                }else {
                    showInputDialog = true
                }
                val user = SignUpRoom(
                    userid = userid,
                    userpassword = userpassword,
                    username = username,
                    useremail = useremail,
                    userphone = userphone,
                    useraddress = useraddress
                )
                // ✅ 중복 체크 추가
                viewModel.addUser(user) { success ->
                    if (success) {
                        Toast.makeText(context, "회원가입 완료되었습니다.", Toast.LENGTH_SHORT).show()
                        navController.navigate("login")
                    } else {
                        // 중복 ID일 경우 다이얼로그 띄우기
                        showDuplicateDialog = true
                    }
                }
            }) {
                Text("추가")
            }
        }
        if (showInputDialog) {
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { showInputDialog = false },
                confirmButton = {
                    Button(onClick = { showInputDialog = false }) {
                        Text("확인")
                    }
                },
                title = { Text("입력 오류") },
                text = { Text("모든 항목을 입력해주세요.") }
            )
        }
        // 중복 ID 다이얼로그
        if (showDuplicateDialog) {
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { showDuplicateDialog = false },
                confirmButton = {
                    Button(onClick = { showDuplicateDialog = false }) {
                        Text("확인")
                    }
                },
                title = { Text("중복 오류") },
                text = { Text("이미 존재하는 ID입니다.") }
            )
        }
    }
}