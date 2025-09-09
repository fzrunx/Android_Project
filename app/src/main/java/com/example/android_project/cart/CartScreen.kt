package com.example.android_project.cart

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.isSelected
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage



@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingCart(navController: NavController, modifier: Modifier = Modifier, viewModel: CartViewModel = viewModel()) {
    val cart by viewModel.cartList.collectAsState()
    val cartUi by viewModel.cartItemsUi.collectAsState()
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
                items(cartUi, key = { it.cartItem.id }) { item ->
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
                                    viewModel.setItemSelected(item.cartItem.id, checked)
                                }
                            )

                            // 1️⃣ 작은 이미지
                            AsyncImage(
                                model = item.cartItem.image,
                                contentDescription = item.cartItem.title,
                                modifier = Modifier
                                    .size(60.dp) // 이미지 크기 조절
                            )

                            // 2️⃣ 제목
                            Text(
                                text = item.cartItem.title,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis
                            )

                            // 3️⃣ 수량 조절
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // 증가
                                IconButton(
                                    onClick = { viewModel.increaseQuantity(item.cartItem) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Text("+", style = MaterialTheme.typography.bodyMedium)
                                }
                                Text(
                                    "${item.cartItem.quantity}",
                                    modifier = Modifier.padding(horizontal = 4.dp)
                                )
                                // 감소
                                IconButton(
                                    onClick = { viewModel.decreaseQuantity(item.cartItem) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Text("-", style = MaterialTheme.typography.titleLarge)
                                }
                            }

                            // 4️⃣ 삭제 버튼
                            IconButton(
                                onClick = { viewModel.deleteCart(item.cartItem) },
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Button(onClick = { viewModel.deleteSelected() }) {
                    Text("선택 삭제")
                }
            }
        }
    }
}
