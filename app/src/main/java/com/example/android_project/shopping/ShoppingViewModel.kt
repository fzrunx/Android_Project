package com.example.android_project.shopping

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

    // ✅ 쇼핑 아이템 검색
    fun fetchShopping(keyword: String) {
        viewModelScope.launch {
            try {
                // ✅ 검색어 저장 (뒤로 갔다가 돌아와도 유지됨)
                _searchKeyword.value = keyword

                // ✅ API 호출
                val response = repository.fetchShopping(query = keyword)
                _shoppingList.value = response.items

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    // ✅ 검색어 직접 설정 가능
    fun setSearchKeyword(keyword: String) {
        _searchKeyword.value = keyword
    }
}

