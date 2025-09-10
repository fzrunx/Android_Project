package com.example.android_project.shopping

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class ShoppingViewModel: ViewModel() {
    // ✅ 검색 결과 리스트 상태
    private val _shoppingList = MutableStateFlow<List<ShoppingItem>>(emptyList())
    val shoppingList: StateFlow<List<ShoppingItem>> = _shoppingList

    // ✅ Repository 연결
    private val repository: ShoppingRepository = ShoppingClient.shoppingRetrofitRepository

    // ✅ 검색어 상태를 ViewModel에서 관리
    private val _searchKeyword = MutableStateFlow("")
    val searchKeyword: StateFlow<String> = _searchKeyword

    // 페이지네이션 관련
    var currentPage by mutableStateOf(0)
        private set
    private val pageSize = 10
    // ✅ 현재 페이지 아이템 상태
    var currentPageItems by mutableStateOf<List<ShoppingItem>>(emptyList())
        private set

    private fun updateCurrentPageItems() {
        val from = currentPage * pageSize
        val to = minOf(from + pageSize, _shoppingList.value.size)
        currentPageItems = _shoppingList.value.subList(from, to)
    }

    // ✅ 쇼핑 아이템 검색
    fun fetchShopping(keyword: String) {
        viewModelScope.launch {
            try {
                // ✅ 검색어 저장 (뒤로 갔다가 돌아와도 유지됨)
                _searchKeyword.value = keyword

                // ✅ API 호출
                val response = repository.fetchShopping(query = keyword, display = 100, start = 1)
                _shoppingList.value = response.items
                currentPage = 0 // 검색 시 항상 1페이지로 초기화
                updateCurrentPageItems()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun nextPage() {
        if ((currentPage + 1) * pageSize < _shoppingList.value.size) {
            currentPage++
            updateCurrentPageItems()
        }
    }

    fun prevPage() {
        if (currentPage > 0) {
            currentPage--
            updateCurrentPageItems()
        }
    }

    fun totalPages(): Int = maxOf(1, (_shoppingList.value.size + pageSize - 1) / pageSize)

    // ✅ 검색어 직접 설정 가능
    fun setSearchKeyword(keyword: String) {
        _searchKeyword.value = keyword
    }
    fun getProductById(productId: String): ShoppingItem? {
        return _shoppingList.value.find { it.productId == productId }
    }
}

