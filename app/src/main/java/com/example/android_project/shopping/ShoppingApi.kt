package com.example.android_project.shopping

import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface ShoppingApi {
    @Headers(
        "X-Naver-Client-Id:1i1G59fuOO9BybJdY8dL",
        "X-Naver-Client-Secret:nP1aEldLVz"
    )
    @GET("v1/search/shop.json")
    suspend fun shopping(
        @Query("query") query: String,
        @Query("display") display: Int = 10,
        @Query("start") start: Int = 1,
        @Query("sort") sort: String ="sim",
        @Query("filter") filter: String = "",
        @Query("exclude") exclude: String = ""
    ): Shopping
}