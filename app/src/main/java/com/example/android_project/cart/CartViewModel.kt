package com.example.android_project.cart

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_project.shopping.ShoppingItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CartViewModel(application: Application): AndroidViewModel(application)  {
    private val dao: CartDao = CartDB.getDatabase(application).cartDao()

    // DB에서 바로 가져오기
    val cartList: StateFlow<List<CartRoom>> = dao.getAllCartItems().stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        emptyList()
    )
    // ✅ 선택 결제용 상태
    private val _selectedForPayment = MutableStateFlow<List<CartRoom>>(emptyList())
    val selectedForPayment: StateFlow<List<CartRoom>> = _selectedForPayment.asStateFlow()

    fun addToCart(item: ShoppingItem, keyword: String) {
        viewModelScope.launch {
            val existing = dao.getCartItemByProductId(item.productId)
            if (existing != null) {
                dao.updateQuantity(existing.id, existing.quantity + 1)
            } else {
                val cartItem = CartRoom(
                    productId = item.productId,
                    title = item.title,
                    image = item.image,
                    link = item.link,
                    keyword = keyword,
                    quantity = 1,
                    lprice = item.lprice
                )
                dao.insert(cartItem)
            }
        }
    }

    fun setItemSelected(itemId: Int, selected: Boolean) = viewModelScope.launch {
        dao.updateIsSelected(itemId, selected)
    }

    fun toggleSelectAll(selectAll: Boolean) = viewModelScope.launch {
        dao.updateAllSelected(selectAll)
    }

    fun deleteSelected() = viewModelScope.launch {
        val toDelete = dao.getAllCartItemsOnce().filter { it.isSelected }
        toDelete.forEach { dao.delete(it) }
    }

    fun increaseQuantity(item: CartRoom) = viewModelScope.launch {
        dao.updateQuantity(item.id, item.quantity + 1)
    }

    fun decreaseQuantity(item: CartRoom) = viewModelScope.launch {
        if (item.quantity > 1) dao.updateQuantity(item.id, item.quantity - 1)
    }
    fun deleteCart(item: CartRoom) = viewModelScope.launch {
        dao.delete(item)
    }
    // ✅ 선택 결제: 선택된 아이템 저장 + 장바구니에서 제거
    fun checkoutSelected() = viewModelScope.launch {
        val selectedItems = cartList.value.filter { it.isSelected }
        _selectedForPayment.value = selectedItems
        deleteSelected() // 장바구니에서 제거
    }
}
