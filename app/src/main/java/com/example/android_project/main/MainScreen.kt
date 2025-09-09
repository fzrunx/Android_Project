package com.example.android_project.main

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import coil.compose.AsyncImage
import com.example.android_project.cart.CartViewModel
import com.example.android_project.shopping.ShoppingViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController, modifier: Modifier = Modifier, viewModel: ShoppingViewModel = viewModel(), cartViewModel: CartViewModel = viewModel()) {
    val context = LocalContext.current
    val shopping by viewModel.shoppingList.collectAsState()
    val searchKeyword by viewModel.searchKeyword.collectAsState()
    val currentPageItems = viewModel.currentPageItems // 현재 페이지 아이템
    val gridState = rememberLazyGridState() // Grid 상태
    val coroutineScope = rememberCoroutineScope() // CoroutineScope

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
        Column(
            modifier = Modifier
                .padding(
                    start = 8.dp,
                    end = 8.dp,
                    top = padding.calculateTopPadding(),       // TopAppBar와 겹치지 않도록
                    bottom = padding.calculateBottomPadding()  // BottomAppBar와 겹치지 않도록
                )
        ) {
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
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),// 한 줄에 2개씩
                state = gridState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f), // 💡 여기가 핵심: Grid가 남는 공간만 차지하도록
                contentPadding = PaddingValues(
                    top = 4.dp,
                    bottom = 0.dp
                )// 여기서 0으로 하면 페이지네이션 버튼과 BottomAppBar 사이 공간 최소화
            ) {
                items(currentPageItems, key = { it.link }) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .aspectRatio(0.666f) // 가로:세로 = 1:1.5
                            .clickable {
                                val encodedUrl = Uri.encode(item.link) // 특수문자 처리
                                navController.navigate("webview/$encodedUrl")
                            }
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            val cleanTitle = item.title.replace(Regex("<.*?>"), "")
                            Text(                          // HTML 태그 제거
                                text = cleanTitle,
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 4,                 // 최대 4줄
                                overflow = TextOverflow.Ellipsis
                            )// 글자 잘림시 ... 처리

                            // 이미지 표시
                            AsyncImage(
                                model = item.image,          // 이미지 URL
                                contentDescription = item.title,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .weight(1f) // 이미지가 카드 안에서 공간을 균등하게 차지
                            )
                            Button(
                                onClick = { cartViewModel.addToCart(item, searchKeyword) },
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            ) {
                                Text("장바구니 추가")
                            }
                        }

                    }
                }
            }
            // 페이지네이션 버튼
            if (currentPageItems.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp), // 위아래 여백 최소화
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                viewModel.prevPage()
                                coroutineScope.launch {
                                    gridState.scrollToItem(0)
                                }
                            },
                            enabled = viewModel.currentPage > 0,
                            contentPadding = PaddingValues(
                                horizontal = 12.dp,
                                vertical = 4.dp
                            ) // 버튼 안쪽 여백 조절
                        ) {
                            Text("이전")
                        }

                        Text(
                            text = if (shopping.isEmpty()) "0/0" else "${viewModel.currentPage + 1} / ${viewModel.totalPages()}",
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )

                        Button(
                            onClick = {
                                viewModel.nextPage()
                                coroutineScope.launch {
                                    gridState.scrollToItem(0)
                                }
                            },
                            enabled = viewModel.currentPage < viewModel.totalPages() - 1,
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text("다음")
                        }
                    }
                }
            }
        }
    }
}