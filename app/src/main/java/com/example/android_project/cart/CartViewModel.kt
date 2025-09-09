package com.example.android_project.cart

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_project.shopping.ShoppingItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CartViewModel(application: Application): AndroidViewModel(application)  {
    private val dao: CartDao = CartDB.getDatabase(application).cartDao()

    val cartList = dao.getAllCartItems().stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        emptyList()
    )

    fun addToCart(item: ShoppingItem, keyword: String) {
        viewModelScope.launch {
            // 1️⃣ DB에서 이미 있는 아이템 찾기
            val existing = dao.getCartItemByProductId(item.productId)
            if (existing != null) {
                // 2️⃣ 이미 있으면 수량만 증가
                dao.updateQuantity(existing.id, existing.quantity + 1)
            } else {
                // 3️⃣ 없으면 새로 추가
                val cartItem = CartRoom(
                    productId = item.productId,
                    title = item.title,
                    image = item.image,
                    link = item.link,
                    keyword = keyword,
                    quantity = 1
                )
                dao.insert(cartItem)
            }
        }
    }
    fun deleteCart(item: CartRoom) = viewModelScope.launch {
        dao.delete(item)
    }

    fun increaseQuantity(item: CartRoom) = viewModelScope.launch {
        dao.updateQuantity(item.id, item.quantity + 1)
    }

    fun decreaseQuantity(item: CartRoom) = viewModelScope.launch {
        if (item.quantity > 1) {
            dao.updateQuantity(item.id, item.quantity - 1)
        }
    }
    // UI용 선택 상태 리스트
    private val _cartItemsUi = MutableStateFlow<List<CartItemUi>>(emptyList())
    val cartItemsUi: StateFlow<List<CartItemUi>> = _cartItemsUi

    init {
        viewModelScope.launch {
            // DB 리스트를 가져와 UI용 리스트로 변환
            cartList.collect { dbItems ->
                _cartItemsUi.value = dbItems.map { CartItemUi(it) }
            }
        }
    }

    fun toggleSelectAll(selectAll: Boolean) {
        _cartItemsUi.value = _cartItemsUi.value.map { it.copy(isSelected = selectAll) }
    }

    fun setItemSelected(itemId: Int, selected: Boolean) = viewModelScope.launch {
        // 1️⃣ UI 상태 업데이트
        _cartItemsUi.value = _cartItemsUi.value.map {
            if (it.cartItem.id == itemId) it.copy(isSelected = selected)
            else it
        }

        // 2️⃣ DB 상태 업데이트
        dao.updateIsSelected(itemId, selected)
    }

    fun deleteSelected() {
        viewModelScope.launch {
            // 체크된 아이템만 DB에서 삭제
            val toDelete = _cartItemsUi.value.filter { it.isSelected }.map { it.cartItem }
            toDelete.forEach { dao.delete(it) }

            // UI에서도 삭제된 아이템 제거
            _cartItemsUi.value = _cartItemsUi.value.filter { !it.isSelected }
        }
    }
}
