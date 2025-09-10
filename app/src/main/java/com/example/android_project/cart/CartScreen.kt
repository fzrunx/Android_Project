package com.example.android_project.cart

import android.annotation.SuppressLint
import android.app.Application
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.android_project.payment.PaymentDB
import com.example.android_project.payment.PaymentViewModel
import com.example.android_project.payment.PaymentViewModelFactory
import com.example.android_project.user.info.SignUpDB
import com.example.android_project.user.info.login.LoginRepository
import com.example.android_project.user.info.login.LoginViewModel
import com.example.android_project.user.info.login.LoginViewModelFactory


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingCart(navController: NavController, modifier: Modifier = Modifier, viewModel: CartViewModel = viewModel(),loginViewModel: LoginViewModel ) {

    // 1️⃣ Context와 DB 가져오기
    val context = LocalContext.current.applicationContext as Application
    val cartDao = CartDB.getDatabase(context).cartDao()
    val paymentDao = PaymentDB.getDatabase(context).paymentDao() // PaymentDB를 만들어야 함

    // 2️⃣ PaymentViewModel 생성
    val paymentViewModel: PaymentViewModel = viewModel(
        factory = PaymentViewModelFactory(context, cartDao, paymentDao)
    )
    val cart by viewModel.cartList.collectAsState()
    val isLoggedIn by loginViewModel.loginState.collectAsState()
    val userId by loginViewModel.currentUserId.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = {
                Text(
                    "장바구니",
                    style = MaterialTheme.typography.titleLarge
                )
            })
        }
    ) { padding ->
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(onClick = { viewModel.toggleSelectAll(true) }) {
                    Text("전체 선택")
                }
                Button(onClick = { viewModel.toggleSelectAll(false) }) {
                    Text("전체 해제")
                }
                Button(onClick = { viewModel.deleteSelected() }) {
                    Text("선택 삭제")
                }
            }
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f) // ← 여기서 weight을 주면 남은 공간만 차지
                    .padding(padding)       // Scaffold padding
                    .systemBarsPadding()    // status bar 포함
            ) {
                items(cart, key = { it.id }) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // ✅ 체크박스
                            Checkbox(
                                checked = item.isSelected,
                                onCheckedChange = { checked ->
                                    viewModel.setItemSelected(item.id, checked)
                                }
                            )

                            // 1️⃣ 작은 이미지
                            AsyncImage(
                                model = item.image,
                                contentDescription = item.title,
                                modifier = Modifier
                                    .size(60.dp) // 이미지 크기 조절
                            )
                            val cleanTitle = item.title.replace(Regex("<.*?>"), "")
                            // 2️⃣ 제목
                            Text(
                                text = cleanTitle,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis
                            )
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // 3️⃣ 수량 조절
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    // 증가
                                    IconButton(
                                        onClick = { viewModel.increaseQuantity(item) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Text("+", style = MaterialTheme.typography.bodyMedium)
                                    }
                                    Text(
                                        "${item.quantity}",
                                        modifier = Modifier.padding(horizontal = 4.dp)
                                    )
                                    // 감소
                                    IconButton(
                                        onClick = { viewModel.decreaseQuantity(item) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Text("-", style = MaterialTheme.typography.titleLarge)
                                    }
                                }
                                // ✅ 가격 표시
                                val price = item.lprice.toIntOrNull() ?: 0      // String → Int 변환(현재 string으로 받아오는중이라 변환 필요)
                                val total = price * item.quantity               // 수량 반영
                                Text(
                                    text = "${total}원",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary, // 가격은 강조색
                                    modifier = Modifier
                                        .padding(top = 4.dp)
                                )
                            }
                            // 4️⃣ 삭제 버튼
                            IconButton(
                                onClick = { viewModel.deleteCart(item) },
                                modifier = Modifier.size(36.dp) // 버튼 전체 크기 조절
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "삭제",
                                    modifier = Modifier.size(24.dp), // 아이콘 크기
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
            // LazyColumn 바로 아래
            Divider(
                color = Color.Gray,
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("총 금액", style = MaterialTheme.typography.bodyLarge)

                val totalAmount = cart
                    .filter { it.isSelected }
                    .sumOf { (it.lprice.toIntOrNull() ?: 0) * it.quantity }

                Text(
                    text = "${totalAmount}원",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Button(onClick = { viewModel.deleteSelected() }) {
                    Text("선택 삭제")
                }
                Button(onClick = {
                    if (isLoggedIn) {
                        // 로그인 되어 있으면 결제 페이지로 이동
                        userId?.let { id ->
                            paymentViewModel.savePaymentHistory(id)
                        } // 결제 기록 저장 + 장바구니 삭제
                        navController.navigate("mypage_Screen")
                    } else {
                        // 로그인 안 되어 있으면 로그인 화면으로 이동
                        navController.navigate("login?redirect=mypage_Screen")
                    }
                },
                    modifier = Modifier.padding(start = 8.dp)
                ) { Text("선택 결제") }
            }
        }
    }
}
