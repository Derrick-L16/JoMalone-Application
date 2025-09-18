//package com.example.jomalonemobileapplication.navigation
//
//import android.net.Uri // Keep for PerfumeTagScreen
//import android.net.http.SslCertificate.saveState
//import android.util.Log
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.remember
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.navigation.NavHostController
//import androidx.navigation.NavType
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.navArgument
//import com.example.jomalonemobileapplication.AuthState
//import com.example.jomalonemobileapplication.LoadingScreen
//import com.example.jomalonemobileapplication.mainPage.JoMaloneMainPage
//import com.example.jomalonemobileapplication.admin.perfume.PerfumeStockScreen
//import com.example.jomalonemobileapplication.admin.user.UserManagementScreen
//import com.example.jomalonemobileapplication.checkIfUserIsAdmin
//import com.example.jomalonemobileapplication.findStartDestination
//import com.example.jomalonemobileapplication.mainmenu.AdminMainMenuScreen
//import com.example.jomalonemobileapplication.product.ProductDetailScreen
//import com.example.jomalonemobileapplication.searchModule.SearchModuleScreen
//import com.example.jomalonemobileapplication.features.tag.PerfumeTagScreen // For PerfumeTagScreen
//
//object AdminDestinations {
//    const val ADMIN_MAIN_MENU = "admin_main_menu"
//    const val USER_MANAGEMENT_SCREEN = "admin_user_management"
//    const val PERFUME_STOCK_SCREEN = "admin_perfume_stock"
//}
//
//@Composable
//fun AppNavigation(
//    navController: NavHostController,
//    modifier: Modifier = Modifier,
//    currentAuthState: AuthState,
//    onMenuClick: () -> Unit
//) {
//    val startDestination: String = remember(currentAuthState) {
//        when (currentAuthState) {
//            is AuthState.Authenticated -> {
//                val user = currentAuthState.user
//                if (checkIfUserIsAdmin(user)) AdminDestinations.ADMIN_MAIN_MENU
//                else "jomalone_main/${user.uid}"
//            }
//            is AuthState.Unauthenticated -> "jomalone_main_default"
//            else -> "auth_loading"
//        }
//    }.also { Log.i("AppNavigation", "Determined Start Destination: $it based on authState=$currentAuthState") }
//
//    NavHost(
//        navController = navController,
//        startDestination = startDestination,
//        modifier = modifier
//    ) {
//        composable("auth_loading") {
//            LoadingScreen(message = "Checking authentication...")
//        }
//        composable("login") {
//            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//                Text("Login Screen Placeholder - Replace with actual LoginScreen")
//            }
//        }
//        composable(
//            route = "jomalone_main/{userId}",
//            arguments = listOf(navArgument("userId") { type = NavType.StringType })
//        ) { backStackEntry ->
//            val userIdFromNav = backStackEntry.arguments?.getString("userId")
//            JoMaloneMainPage(
//                navController = navController,
//                userIdToShowRecommendationsFor = userIdFromNav,
//                onMenuClick = onMenuClick
//            )
//        }
//        composable("jomalone_main_default") {
//            JoMaloneMainPage(
//                navController = navController,
//                userIdToShowRecommendationsFor = null,
//                onMenuClick = onMenuClick
//            )
//        }
//        composable(
//            route = "productDetail/{perfumeId}",
//            arguments = listOf(navArgument("perfumeId") { type = NavType.StringType; nullable = true })
//        ) { backStackEntry ->
//            ProductDetailScreen(
//                perfumeId = backStackEntry.arguments?.getString("perfumeId"),
//                onNavigateBack = { navController.popBackStack() }
//            )
//        }
//        composable("search_module_screen") {
//            SearchModuleScreen(
//                navController = navController
//            )
//        }
//        composable(AdminDestinations.ADMIN_MAIN_MENU) {
//            AdminMainMenuScreen(
//                navController = navController
//            )
//        }
//        composable(AdminDestinations.USER_MANAGEMENT_SCREEN) {
//            UserManagementScreen(navController = navController)
//        }
//        composable(AdminDestinations.PERFUME_STOCK_SCREEN) {
//            PerfumeStockScreen(
//                navController = navController,
//                onNavigateBack = { navController.popBackStack() }
//            )
//        }
//
//        // --- Routes now using hardcoded strings ---
//        composable("profile_screen/{userId}") { backStackEntry ->
//            val userId = backStackEntry.arguments?.getString("userId")
//            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Profile Screen for $userId") }
//        }
//        composable("settings_screen") {
//            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Settings Screen") }
//        }
//        composable("about_screen") {
//            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("About Screen") }
//        }
//        composable("order_history_screen") {
//            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Order History Screen") }
//        }
//
//        // --- PerfumeTagScreen route now using a hardcoded base string ---
//        composable(
//            route = "perfumes_by_tag/{tagForQuery}?title={displayTitle}", // MODIFIED to hardcoded base
//            arguments = listOf(
//                navArgument("tagForQuery") { type = NavType.StringType },
//                navArgument("displayTitle") { type = NavType.StringType }
//            )
//        ) { backStackEntry ->
//            val tag = backStackEntry.arguments?.getString("tagForQuery")
//            val encodedTitle = backStackEntry.arguments?.getString("displayTitle")
//            val displayTitle = encodedTitle?.let { Uri.decode(it) }
//
//            if (tag != null && displayTitle != null) {
//                PerfumeTagScreen(
//                    navController = navController,
//                    tagForQuery = tag,
//                    displayTitle = displayTitle
//                )
//            } else {
//                Log.e("AppNavigation", "PerfumeTagScreen: Missing arguments. Tag: $tag, Title: $encodedTitle")
//                Box(Modifier.fillMaxSize(), Alignment.Center) { Text("Error: Category details missing.") }
//            }
//        }
//
//        composable("error_screen/{message}") { backStackEntry ->
//            val message = backStackEntry.arguments?.getString("message") ?: "Unknown error"
//            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Error: $message") }
//        }
//    }
//
//    LaunchedEffect(currentAuthState, navController, startDestination) {
//        val currentRoute = navController.currentDestination?.route
//        Log.d("AppNavigation_AuthStateEffect", "Auth State Changed: $currentAuthState, Current Route: $currentRoute")
//        when (currentAuthState) {
//            is AuthState.Authenticated -> {
//                val user = currentAuthState.user
//                val isAdmin = checkIfUserIsAdmin(user)
//                val targetRoute = if (isAdmin) AdminDestinations.ADMIN_MAIN_MENU else "jomalone_main/${user.uid}"
//                if (currentRoute == "auth_loading" || currentRoute == "login" || currentRoute == "jomalone_main_default") {
//                    Log.d("AppNavigation_AuthStateEffect", "Authenticated: Navigating to $targetRoute from $currentRoute")
//                    navController.navigate(targetRoute) {
//                        popUpTo(navController.graph.findStartDestination().id) { inclusive = true; saveState = true }
//                        launchSingleTop = true; restoreState = true
//                    }
//                } else if (isAdmin && !currentRoute.toString().startsWith("admin_") && currentRoute != targetRoute) {
//                    Log.d("AppNavigation_AuthStateEffect", "Admin on non-admin page ($currentRoute), redirecting to ${AdminDestinations.ADMIN_MAIN_MENU}")
//                    navController.navigate(AdminDestinations.ADMIN_MAIN_MENU) {
//                        popUpTo(navController.graph.findStartDestination().id) { inclusive = true }; launchSingleTop = true
//                    }
//                } else if (!isAdmin && currentRoute.toString().startsWith("admin_")) {
//                    Log.d("AppNavigation_AuthStateEffect", "Non-admin on admin page ($currentRoute), redirecting to jomalone_main/${user.uid}")
//                    navController.navigate("jomalone_main/${user.uid}") {
//                        popUpTo(navController.graph.findStartDestination().id) { inclusive = true }; launchSingleTop = true
//                    }
//                }
//            }
//            is AuthState.Unauthenticated -> {
//                val guestRoute = "jomalone_main_default"
//                // --- Public route check now using hardcoded "perfumes_by_tag" ---
//                val isPublicRoute = currentRoute == guestRoute ||
//                        currentRoute == "login" ||
//                        currentRoute == "auth_loading" ||
//                        currentRoute?.startsWith("productDetail") == true ||
//                        currentRoute?.startsWith("perfumes_by_tag") == true || // MODIFIED
//                        currentRoute?.startsWith("error_screen") == true
//
//                if (!isPublicRoute) {
//                    Log.d("AppNavigation_AuthStateEffect", "Unauthenticated: Navigating to $guestRoute from $currentRoute")
//                    navController.navigate(guestRoute) {
//                        popUpTo(navController.graph.findStartDestination().id) { inclusive = true }; launchSingleTop = true
//                    }
//                }
//            }
//            is AuthState.Loading -> {
//                if (currentRoute != "auth_loading") {
//                    Log.d("AppNavigation_AuthStateEffect", "Loading Auth: Navigating to auth_loading from $currentRoute")
//                    navController.navigate("auth_loading") {
//                        popUpTo(navController.graph.findStartDestination().id) { inclusive = true }; launchSingleTop = true
//                    }
//                }
//            }
//            is AuthState.Error -> {
//                Log.e("AppNavigation_AuthStateEffect", "Auth Error occurred: ${currentAuthState.message}")
//            }
//        }
//    }
//}
