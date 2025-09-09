package com.example.android_project.cart

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingCart(navController: NavController, modifier: Modifier = Modifier, viewModel: CartViewModel = viewModel()) {
    val cart by viewModel.cartList.collectAsState()
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = {
                Text(
                    "장바구니",
                    style = MaterialTheme.typography.titleLarge
                )
            })
        }
    ){ padding ->
        LazyColumn(
        modifier = Modifier.padding(padding)
    ) {
        items(cart) { item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable {
                        val encodedUrl = Uri.encode(item.link) // 특수문자 처리
                        navController.navigate("webview/$encodedUrl")
                    }
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(text = item.title, style = MaterialTheme.typography.bodyMedium)
                    // 이미지 표시
                    AsyncImage(
                        model = item.image,          // 이미지 URL
                        contentDescription = item.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // 수량 조절
                        Row {
                            Button(onClick = { viewModel.decreaseQuantity(item) }) { Text("-") }
                            Text("${item.quantity}", modifier = Modifier.padding(horizontal = 8.dp))
                            Button(onClick = { viewModel.increaseQuantity(item) }) { Text("+") }
                        }

                        // 삭제 버튼
                        Button(onClick = { viewModel.deleteCart(item) }) {
                            Text("삭제")
                        }
                    }
                }
            }
            }
        }
     }
}

