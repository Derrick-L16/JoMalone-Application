package com.example.jomalonemobileapplication.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.jomalonemobileapplication.core.ui.CheckoutRoute
import com.example.jomalonemobileapplication.core.ui.ShoppingCartRoute
import com.example.jomalonemobileapplication.feature.login.ui.AdminScreen
import com.example.jomalonemobileapplication.feature.login.ui.AuthViewModel
import com.example.jomalonemobileapplication.feature.login.ui.ForgotPasswordScreen
import com.example.jomalonemobileapplication.feature.login.ui.SignIn
import com.example.jomalonemobileapplication.feature.login.ui.SignUp
import com.example.jomalonemobileapplication.feature.profile.ui.ContactUsScreen
import com.example.jomalonemobileapplication.feature.profile.ui.FavouritePerfumeScreen
import com.example.jomalonemobileapplication.feature.profile.ui.ProfileContent
import com.example.jomalonemobileapplication.feature.profile.ui.ProfileInformationScreen
import com.example.jomalonemobileapplication.feature.scentTest.presentation.ScentTestScreen
import com.example.jomalonemobileapplication.mainPage.JoMaloneMainPage
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.jomalonemobileapplication.AppDatabase
import com.example.jomalonemobileapplication.core.data.entity.Order
import com.example.jomalonemobileapplication.core.data.mapper.CartItemMapper
import com.example.jomalonemobileapplication.core.data.repository.CartRepositoryImpl
import com.example.jomalonemobileapplication.core.data.repository.OrderRepository
import com.example.jomalonemobileapplication.core.ui.CartViewModel
import com.example.jomalonemobileapplication.core.ui.CartViewModelFactory
import com.example.jomalonemobileapplication.core.ui.JoMaloneOrderSuccessScreen
import com.example.jomalonemobileapplication.feature.perfumeCustomization.presentation.CustomizationFlow
import com.example.jomalonemobileapplication.feature.scentTest.domain.model.ScentResultRepository
import com.example.jomalonemobileapplication.feature.scentTest.domain.model.ScentType
import com.example.jomalonemobileapplication.feature.scentTest.presentation.ScentResultScreen
import com.example.jomalonemobileapplication.feature.scentTest.presentation.ScentTestViewModel
import com.example.jomalonemobileapplication.core.ui.PaymentHistoryRoute
import com.example.jomalonemobileapplication.feature.profile.ui.CustomizationHistoryScreen
import com.example.jomalonemobileapplication.feature.profile.ui.ScentPreferenceScreen

@Composable
fun NavigationApp(
    authViewModel: AuthViewModel,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {

    NavHost(
        navController = navController,
        startDestination = "SignIn"
    ) {
        // Profile module
        composable("SignIn") {
            SignIn(
                onNavigateToHome = {
                    authViewModel.loadUserProfile()   // **
                    navController.navigate("HomePage") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToSignUp = {
                    navController.navigate("SignUp")
                },
                onNavigateToForgotPassword = {
                    navController.navigate("ForgotPassword")
                },
                onNavigateToAdmin = {
                    navController.navigate("Admin")
                }
            )

        }

        composable("SignUp") {
            SignUp(onNavigateToSignIn = {
                navController.navigate("SignIn") {
                    popUpTo("SignIn") { inclusive = true }
                }
            }
            )
        }

        composable("ForgotPassword") {
            ForgotPasswordScreen(
                onNavigateToSignIn = {
                    navController.navigate("SignIn") {
                        popUpTo("ForgotPassword") { inclusive = true }
                    }
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("Admin") {
            AdminScreen(
                navController = navController
            )
        }

        composable("HomePage"){
            LaunchedEffect(Unit) {
                authViewModel.loadUserProfile()
            }

            JoMaloneMainPage(
                onNavigateToShoppingCart = {
                    navController.navigate("ShoppingCart")
                },
                modifier = modifier
            )
        }

        // Update the Profile composable
        composable("Profile") {
            ProfileContent(
                onNavigationToProfileInformation = {
                    navController.navigate("ProfileInformation")
                },
                onNavigateToFavouritePerfume = {
                    navController.navigate("FavouritePerfume")
                },
                onNavigateToPaymentHistory = {
                    navController.navigate("PaymentHistory")
                },
                onNavigateToContactUs = {
                    navController.navigate("ContactUs")
                },
                onNavigateToScentPreference = {
                    navController.navigate("ScentPreference")
                },
                onNavigateToCustomizationHistory = {
                    navController.navigate("CustomizationHistory")
                },
                onAccountDeleted = {
                    navController.navigate("SignIn"){
                        popUpTo(0){inclusive = true}
                    }
                },
                onNavigateToLogout = {
                    // Sign out and navigate to login
                    navController.navigate("SignIn") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                modifier = modifier
            )
        }

        composable("ProfileInformation") {
            ProfileInformationScreen(
                onBack = { navController.popBackStack() },
                onSaveSuccess = { navController.popBackStack() },
                modifier = modifier
            )
        }

        composable("FavouritePerfume") {
            FavouritePerfumeScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable("PaymentHistory") {
            PaymentHistoryRoute(
                authViewModel = authViewModel,
                modifier = modifier
            )
        }

        composable("ContactUs") {
            ContactUsScreen(
                onBack = { navController.popBackStack() },
                modifier = modifier
            )
        }

        composable("ScentTest"){
//            TestScreenNavigation()
            val scentTestViewModel: ScentTestViewModel = viewModel()

            ScentTestScreen(
                viewModel = scentTestViewModel,
                onTestComplete = { result ->
                    authViewModel.updateUserScentPreference(result) {
                        navController.navigate("ScentResult/${result.name}")
                    }
//                    navController.navigate("ScentResult/${result.name}")
                },
                onNavigateToMain = {
                    navController.navigate("HomePage") {
                        popUpTo("ScentTest") { inclusive = true }
                    }
                }
            )
        }

        composable("ScentPreference") {
            ScentPreferenceScreen(
                onBack = { navController.popBackStack() },
                onNavigateToScentTest = {
                    navController.navigate("ScentTest") {
                        popUpTo("ScentPreference") { inclusive = true }
                    }
                }
            )
        }

        composable("ScentResult/{scentType}") { backStackEntry ->
            val scentTypeString = backStackEntry.arguments?.getString("scentType")
//            val scentType = scentTypeString?.let {
//                enumValueOf<ScentType>(it)
//            } ?: ScentType.CITRUS

            val scentType = scentTypeString?.let {
                try {
                    enumValueOf<ScentType>(it)
                } catch (e: IllegalArgumentException) {
                    ScentType.CITRUS // fallback
                }
            } ?: ScentType.CITRUS

            val scentTestResult = ScentResultRepository.getResultDescription(scentType)

            ScentResultScreen(
                result = scentTestResult,
                onCustomizeClick = {
                    navController.navigate("PerfumeCustomization")
                },
                onRetakeTest = {
                    // Go back to scent test
                    navController.navigate("ScentTest") {
                        popUpTo("ScentTest") { inclusive = true }
                    }
                },
                onGoToMain = {
                    navController.navigate("HomePage") {
                        popUpTo("ScentTest") { inclusive = true }
                    }
                },
                onSavePreference = { scentType ->
//                    authViewModel.updateUserScentPreference(scentType)
//                    {
//                    }
                }
            )
        }

        composable("PerfumeCustomization") {
            val context = LocalContext.current
            val database = remember { AppDatabase.getDatabase(context) }
            val repository = remember {
                CartRepositoryImpl(database.cartItemDao(), CartItemMapper())
            }
            val factory = remember { CartViewModelFactory(repository) }

//            val customizationViewModel: CustomizationViewModel = viewModel()    // delete later
            val cartViewModel: CartViewModel = viewModel(factory = factory)

            val userId = authViewModel.getCurrentUserId()
            CustomizationFlow(
//                viewModel = customizationViewModel,
                cartViewModel = cartViewModel,
                userId = userId,
                onComplete = {
                    navController.navigate("HomePage") {
                        popUpTo("PerfumeCustomization") { inclusive = true }
                    }
                },
                onNavigateToMain = {
                    navController.navigate("HomePage") {
                        popUpTo("PerfumeCustomization") { inclusive = true }
                    }
                }
            )
        }

        composable("CustomizationHistory") {
            val userId = authViewModel.getCurrentUserId()
            if (userId != null) {
                CustomizationHistoryScreen(
                    userId = userId,
                    onBack = { navController.popBackStack() }
                )
            } else {
                // Handle case where user is not logged in
                LaunchedEffect(Unit) {
                    navController.navigate("SignIn") {
                        popUpTo("Profile") { inclusive = true }
                    }
                }
            }
        }

        composable("ShoppingCart") {
            ShoppingCartRoute(
                onNavigateToCheckout = { navController.navigate("Checkout")
                },
                onItemClick = {},  // Navigate to products page
                onBack = {
                    navController.popBackStack() // add back navigation
                },
                useSampleData = false,
                modifier = modifier
            )
        }

        composable("Checkout") {
            CheckoutRoute(
                onCheckoutSuccess = { orderId ->
                    navController.navigate("OrderSuccess/$orderId")
                },
                authViewModel = authViewModel,
                modifier = modifier
            )
        }


        composable("OrderSuccess/{orderId}") { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: ""

            // fetch order from DB/repository by orderId
            val database = AppDatabase.getDatabase(LocalContext.current)
            val orderRepository = OrderRepository(database.orderDao())

            val order by produceState<Order?>(initialValue = null, orderId) {
                value = orderRepository.getOrderById(orderId) // suspend call
            }

            order?.let {
                JoMaloneOrderSuccessScreen(
                    orderNumber = it.orderId,
                    orderTotal = it.total,
                    estimatedDelivery = it.estimatedDelivery.toString(),
                    onGoToHome = { navController.navigate("HomePage") },
                    onViewOrderHistory = { navController.navigate("PaymentHistory") }
                )
            }
        }




    }
}