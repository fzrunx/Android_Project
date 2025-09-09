package com.example.android_project.shopping

import androidx.room.Entity
import androidx.room.PrimaryKey


data class Shopping(
    val lastBuildDate: String,
    val total: Int,
    val start: Int,
    val display: Int,
    val items: List<ShoppingItem>
)

data class ShoppingItem(
    val title: String,
    val link: String,
    val image: String,
    val lprice: String,
    val hprice: String,
    val mallName: String,
    val productId: String,
    val productType: String,
    val maker: String,
    val brand: String,
    val category1: String, // 대분류
    val category2: String, // 중분류
    val category3: String, // 소분류
    val category4: String, // 세분류
)

@Entity(tableName = "shopping_cart")  // 즉 여기 엔티티로 가져올때 위에 내가 필요한 Items에 데이터만 가져오게 작성하면됨
data class ShoppingCartItems(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val productId: String,
    val title: String,
    val image: String,
    val link: String,
    val keyword: String,          // 검색어
)