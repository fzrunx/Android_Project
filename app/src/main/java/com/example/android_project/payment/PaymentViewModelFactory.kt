package com.example.android_project.payment

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android_project.cart.CartDao

class PaymentViewModelFactory (
    private val application: Application,
    private val cartDao: CartDao,
    private val paymentDao: PaymentDao
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PaymentViewModel::class.java)) {
            return PaymentViewModel(application, cartDao, paymentDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}