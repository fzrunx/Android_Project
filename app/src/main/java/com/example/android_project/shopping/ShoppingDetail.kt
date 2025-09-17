package com.example.android_project.shopping

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.android_project.cart.CartViewModel
import com.example.android_project.payment.PaymentViewModel
import com.example.android_project.user.info.login.LoginViewModel

@Composable
fun ProductDetailScreen(navController: NavController, productId: String, viewModel: ShoppingViewModel,
                        cartViewModel: CartViewModel, loginViewModel: LoginViewModel, paymentViewModel: PaymentViewModel, modifier: Modifier = Modifier) {
    val item = viewModel.getProductById(productId) // ViewModel에서 item 가져오기
    val searchKeyword by viewModel.searchKeyword.collectAsState()
    val cart by cartViewModel.cartList.collectAsState()
    val isLoggedIn by loginViewModel.loginState.collectAsState()
    val userId by loginViewModel.currentUserId.collectAsState()


    if (item == null) {
        Text("상품 정보를 불러올 수 없습니다.")
        return
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Image(
            painter = rememberAsyncImagePainter(item.image),
            contentDescription = item.title,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        val cleanTitle = item.title.replace(Regex("<.*?>"), "")
        Text(text = cleanTitle, style = MaterialTheme.typography.titleLarge)
        Text(text = "가격: ${item.lprice}원", style = MaterialTheme.typography.bodyLarge)
        Text(text = "브랜드: ${item.brand}", style = MaterialTheme.typography.bodyMedium)
        Text(text = "제조사: ${item.maker}", style = MaterialTheme.typography.bodyMedium)
        Text(
            text = "카테고리: ${item.category1} > ${item.category2} > ${item.category3} > ${item.category4}"
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.End
        ){
            Button(
                onClick = { cartViewModel.addToCart(item, searchKeyword) },
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Text("장바구니 추가")
            }
            Button(onClick = {
                if (isLoggedIn) {
                    userId?.let { id ->
                        paymentViewModel.paySingleProductDirectly(id, item)
                    }
                    navController.navigate("mypage_Screen")
                } else {
                    navController.navigate("login?redirect=mypage_Screen")
                }
            }) {
                Text("결제")
            }
        }
    }

}
