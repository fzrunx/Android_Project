package com.example.android_project.main

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import com.example.android_project.cart.CartViewModel
import com.example.android_project.shopping.ShoppingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController, modifier: Modifier = Modifier, viewModel: ShoppingViewModel = viewModel(), cartViewModel: CartViewModel = viewModel()) {
    val context = LocalContext.current
    val shopping by viewModel.shoppingList.collectAsState()
    val searchKeyword by viewModel.searchKeyword.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = {
                Text(
                    "쇼핑몰",
                    style = MaterialTheme.typography.titleLarge
                )
            })
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // 검색창
            OutlinedTextField(
                value = searchKeyword,
                onValueChange = { viewModel.setSearchKeyword(it) }, // ✅ ViewModel에 상태 저장
                label = { Text("검색어 입력") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )

            // 검색 버튼
            Button(
                onClick = {
                    if (searchKeyword.isNotBlank()) {
                        viewModel.fetchShopping(searchKeyword)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                Text("검색")
            }
            LazyColumn(
                modifier = Modifier.padding(padding)
            ) {
                items(shopping, key = { it.link }) { item ->
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
                            Button(onClick = { cartViewModel.addToCart(item, searchKeyword) }) {
                                Text("장바구니 추가")
                            }
                        }

                    }
                }

            }
        }
    }
}