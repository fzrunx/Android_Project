package com.example.android_project.shopping

class ShoppingRepository(private val retrofitApi: ShoppingApi) {
    suspend fun fetchShopping(query: String): Shopping =
        retrofitApi.shopping(query)
}