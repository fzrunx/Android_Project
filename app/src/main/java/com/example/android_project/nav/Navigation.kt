package com.example.android_project.nav

import com.example.android_project.nav.TopAppBar
import android.app.Application
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.android_project.cart.CartDB
import com.example.android_project.cart.CartViewModel
import com.example.android_project.cart.ShoppingCart
import com.example.android_project.main.MainScreen
import com.example.android_project.mypage.MyPageScreen
import com.example.android_project.payment.PaymentDB
import com.example.android_project.payment.PaymentViewModel
import com.example.android_project.payment.PaymentViewModelFactory
import com.example.android_project.shopping.ProductDetailScreen
import com.example.android_project.shopping.ShoppingViewModel
import com.example.android_project.shopping.ShoppingWebView
import com.example.android_project.user.info.SignUp
import com.example.android_project.user.info.login.LoginScreen
import com.example.android_project.user.info.login.LoginViewModel
import com.example.android_project.user.info.login.LoginViewModelFactory


@Composable
fun Navigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val context = LocalContext.current

    // 뷰모델 생성
    val shoppingViewModel: ShoppingViewModel =
        viewModel(factory = ViewModelProvider.AndroidViewModelFactory(context.applicationContext as Application))
    val cartViewModel: CartViewModel =
        viewModel(factory = ViewModelProvider.AndroidViewModelFactory(context.applicationContext as Application))

    val dao = com.example.android_project.user.info.SignUpDB.getInstance(context).signUpDao()
    val repository = com.example.android_project.user.info.login.LoginRepository(dao)
    val loginViewModel: LoginViewModel = viewModel(factory = LoginViewModelFactory(context, repository))
    // DB에서 Dao 가져오기
    val paymentDB = PaymentDB.getDatabase(context)
    val paymentDao = paymentDB.paymentDao()

// Factory 사용해서 ViewModel 생성
    val paymentViewModel: PaymentViewModel = viewModel(
        factory = PaymentViewModelFactory(
            application = context.applicationContext as Application,
            cartDao = CartDB.getDatabase(context).cartDao(),
            paymentDao = paymentDao
        )
    )

//    // 자동 로그인 체크
//    LaunchedEffect(Unit) {
//        loginViewModel.checkAutoLogin()
//    }

    // 현재 화면 route 가져오기
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // 상단바, 하단바 표시 여부 결정
    val showBars = currentRoute?.let { it != "login" } ?: true

    val isLoggedIn by loginViewModel.loginState.collectAsState()
    val userId by loginViewModel.currentUserId.collectAsState()
//    val isLoading by loginViewModel.isLoading.collectAsState()


    Scaffold(
        topBar = {  if (showBars) TopAppBar(navController, isLoggedIn, userId, loginViewModel) },
        bottomBar = { if (showBars) BottomAppBar(navController) }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "main_Screen",
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .systemBarsPadding(),
        ) {
            composable( "shopping_Detail/{productId}",
                arguments = listOf(
                    navArgument("productId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: ""
                ProductDetailScreen(
                    navController = navController,
                    productId = productId,
                    viewModel = shoppingViewModel,
                    cartViewModel = cartViewModel,
                    loginViewModel = loginViewModel,
                    paymentViewModel = paymentViewModel)
            }
            composable("mypage_Screen") { MyPageScreen(navController = navController, loginViewModel = loginViewModel, cartViewModel = cartViewModel) }
            composable("cart_Screen") { ShoppingCart(navController = navController,  viewModel = cartViewModel, loginViewModel = loginViewModel) }
            composable("main_Screen") { MainScreen(navController = navController, viewModel = shoppingViewModel, cartViewModel = cartViewModel) }
            composable("login?redirect={redirect}",
                arguments = listOf(navArgument("redirect") {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = true
                })
            ) { backStackEntry ->
                val redirect = backStackEntry.arguments?.getString("redirect")
                LoginScreen(navController = navController, viewModel = loginViewModel, redirect = redirect)
            }
            composable("sign_Up") { SignUp(navController = navController) }
            composable("webview/{url}") { backStackEntry ->
                val url = backStackEntry.arguments?.getString("url") ?: ""
                ShoppingWebView(url = url)
            }
        }
    }
}