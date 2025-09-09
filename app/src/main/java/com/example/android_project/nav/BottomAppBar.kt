package com.example.android_project.nav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomAppBar(navController: NavController) {
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("홈") },
            selected = currentRoute == "main_Screen",
            onClick = { navController.navigate("main_Screen") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "cart") },
            label = { Text("장바구니") },
            selected = currentRoute == "cart_Screen",
            onClick = { navController.navigate("cart_Screen") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "cart") },
            label = { Text("마이페이지") },
            selected = currentRoute == "cart_Screen",
            onClick = { navController.navigate("cart_Screen") }
        )
    }
}