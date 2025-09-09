package com.example.android_project.cart

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_project.shopping.ShoppingItem
import kotlinx.coroutines.flow.SharingStarted
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
            val cartItem = CartRoom(
                productId = item.productId,
                title = item.title,
                image = item.image,
                link = item.link,
                keyword = keyword
            )
            dao.insert(cartItem)

        }
    }

    fun removeCart(item: CartRoom) {
        viewModelScope.launch {
            dao.delete(item)
        }
    }
}