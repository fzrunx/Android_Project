package com.example.android_project.shopping

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ShoppingClient {
    private const val BASE_URL = "https://openapi.naver.com/"

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val api: ShoppingApi = retrofit.create(ShoppingApi::class.java)
    val shoppingRetrofitRepository: ShoppingRepository = ShoppingRepository(api)

}