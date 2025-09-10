package com.example.android_project.mypage

import android.R.attr.text
import android.app.Application
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.navigation.NavController
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.util.TableInfo
import coil.compose.AsyncImage
import com.example.android_project.cart.CartDB
import com.example.android_project.cart.CartViewModel
import com.example.android_project.payment.PaymentDB
import com.example.android_project.payment.PaymentViewModel
import com.example.android_project.payment.PaymentViewModelFactory
import com.example.android_project.user.info.login.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPageScreen(navController: NavController, modifier: Modifier = Modifier, loginViewModel: LoginViewModel, cartViewModel: CartViewModel = viewModel()) {
    val context = LocalContext.current.applicationContext as Application
    val cartDao = CartDB.getDatabase(context).cartDao()
    val paymentDao = PaymentDB.getDatabase(context).paymentDao()

    // Factory로 ViewModel 생성
    val paymentViewModel: PaymentViewModel = viewModel(
        factory = PaymentViewModelFactory(context, cartDao, paymentDao)
    )
    val isLoggedIn by loginViewModel.loginState.collectAsState()
    val userId by loginViewModel.currentUserId.collectAsState()
    val selectedItems by cartViewModel.selectedForPayment.collectAsState()
    val paymentHistory by paymentViewModel.paymentsByUser.collectAsState()
    val cart by cartViewModel.cartList.collectAsState()
    val groupedPayments = paymentHistory.groupBy { it.paymentGroupId }
    LaunchedEffect(userId) {
        userId?.let { paymentViewModel.loadPayments(it) }
    }

    // MyPageScreen에서 로그인 체크
    LaunchedEffect(isLoggedIn) {
        if (!isLoggedIn) {
            navController.navigate("login?redirect=mypage_Screen") {
                popUpTo("mypage_Screen") { inclusive = true }
            }
        }
    }
    // 로그인 되어있으면 화면 표시
    if (isLoggedIn) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "$userId 님의 마이페이지",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                )
            },
            content = { paddingValues ->
                val scrollState = rememberScrollState() // ✅ 스크롤 상태 저장
                Column(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(scrollState) // ✅ 스크롤 가능하게 설정
                ) {
                    Divider(
                        color = Color.Gray,
                        thickness = 1.dp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    Text(
                        text = "$userId 님의 결제내역",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.CenterHorizontally)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (paymentHistory.isEmpty()) {
                        Text(
                            "선택 결제 내역이 없습니다.",
                            modifier = Modifier
                                .padding(16.dp)
                                .align(Alignment.CenterHorizontally)
                        )
                    } else {
                        groupedPayments.forEach { (_, groupItems) ->
                            groupItems.forEach { item ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(4.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
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
                                            modifier = Modifier.weight(1f)
                                                .padding(horizontal = 8.dp),
                                            maxLines = 3,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Column {
                                            Text("${item.quantity}개")
                                            // ✅ 가격 표시
                                            val price = item.lprice.toIntOrNull()
                                                ?: 0      // String → Int 변환(현재 string으로 받아오는중이라 변환 필요)
                                            val total = price * item.quantity               // 수량 반영
                                            Text(
                                                text = "${total}원",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.primary, // 가격은 강조색
                                                modifier = Modifier
                                                    .padding(top = 4.dp)
                                            )
                                        }
                                    }
                                }
                            }
                                val totalAmount = groupItems.sumOf { (it.lprice.toIntOrNull() ?: 0) * it.quantity }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "총 결제한 금액: $totalAmount 원",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            Divider(
                                color = Color.Gray,
                                thickness = 1.dp,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        )
    }
}
