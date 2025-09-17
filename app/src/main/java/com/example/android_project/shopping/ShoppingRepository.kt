package com.example.android_project.shopping

class ShoppingRepository(private val retrofitApi: ShoppingApi) {
    // ✅ display와 start를 인자로 받을 수 있도록 수정
    suspend fun fetchShopping(
        query: String,
        display: Int = 100,  // 한 번에 불러올 개수
        start: Int = 1       // 시작 인덱스
    ): Shopping {
        return retrofitApi.shopping(
            query = query,
            display = display,
            start = start
        )
    }
}