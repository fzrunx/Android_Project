package com.example.android_project.payment

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_project.cart.CartDao
import com.example.android_project.cart.CartRoom
import com.example.android_project.shopping.ShoppingItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class PaymentViewModel(application: Application, private val cartDao: CartDao, private val paymentDao: PaymentDao): AndroidViewModel(application) {
    private val _paymentsByUser = MutableStateFlow<List<PaymentRoom>>(emptyList())
    val paymentsByUser: StateFlow<List<PaymentRoom>> get() = _paymentsByUser
    // 1️⃣ 유저별 결제 내역 가져오기
    fun loadPayments(userId: String) {
        viewModelScope.launch {
            paymentDao.getPaymentsByUser(userId).collect { payments ->
                _paymentsByUser.value = payments
            }
        }
    }

    // 2️⃣ 결제 처리 + 장바구니 삭제
    fun savePaymentHistory(userId: String) = viewModelScope.launch {
        val selectedItems: List<CartRoom> = cartDao.getAllCartItemsOnce().filter { it.isSelected }
        if (selectedItems.isEmpty()) return@launch

        val groupId = UUID.randomUUID().toString()

        // 1. Payment DB에 insert
        selectedItems.forEach { item ->
            val payment = PaymentRoom(
                userId = userId,
                productId = item.productId,
                title = item.title,
                image = item.image,
                link = item.link,
                quantity = item.quantity,
                lprice = item.lprice,
                keyword = item.keyword,
                paymentGroupId = groupId
            )
            paymentDao.insertPayment(payment)
        }

        // 2. 장바구니 삭제
        selectedItems.forEach { cartDao.delete(it) }

        // 3. 결제 내역 갱신
        loadPayments(userId)
    }
    fun paySingleProductDirectly(userId: String, item: ShoppingItem) = viewModelScope.launch {
        val groupId = UUID.randomUUID().toString()
        val payment = PaymentRoom(
            userId = userId,
            productId = item.productId,
            title = item.title,
            image = item.image,
            link = item.link,
            quantity = 1,
            lprice = item.lprice,
            keyword = item.keyword ?: "검색어없음", // <- null/빈값 대비
            paymentGroupId = groupId
        )
        paymentDao.insertPayment(payment)
        loadPayments(userId)
    }
}