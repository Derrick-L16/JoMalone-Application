package com.example.jomalonemobileapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.jomalonemobileapplication.theme.Background
import com.example.jomalonemobileapplication.navigation.NavigationApp
import com.example.jomalonemobileapplication.theme.JoMaloneMobileApplicationTheme
import com.example.jomalonemobileapplication.feature.login.ui.AuthViewModel
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.LayoutDirection
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.jomalonemobileapplication.core.ui.CartViewModel
import com.example.jomalonemobileapplication.theme.Cormorant

@Composable
fun LoadingScreen(message: String = "Loading...") {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(8.dp))
            Text(message, style = MaterialTheme.typography.bodyLarge)
        }
    }
}
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JoMaloneMobileApplicationTheme {
                val authViewModel: AuthViewModel = viewModel()
                val cartViewModel: CartViewModel = viewModel()
                val navController = rememberNavController()

                // Get current route
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                // Define routes where you want to hide the bars
                val routesWithoutBars = listOf(
                    "SignIn",
                    "SignUp",
                    "ForgotPassword",
                    "Admin",
                    "ScentTest",
                    "PerfumeCustomization",
                    "ScentPreference",
                    "CustomizationHistory"
                )

                // Define routes where you want to hide only the bottom bar
                val routesWithoutBottomBar = listOf(
                    "ProfileInformation",
                    "FavouritePerfume",
                    "PaymentHistory",
                    "ContactUs",
                    "ShoppingCart",
                    "Checkout",
                    "ScentTest",
                    "PerfumeCustomization",
                    "ScentPreference",
                    "CustomizationHistory"

                )

                val shouldShowTopBar = currentRoute !in routesWithoutBars
                val shouldShowBottomBar = currentRoute !in routesWithoutBars &&
                        currentRoute !in routesWithoutBottomBar

                Scaffold(
                    topBar = {
                        if (shouldShowTopBar) {
                            JoMaloneHeader(navController)
                        }
                    },
                    bottomBar = {
                        if (shouldShowBottomBar) {
                            BottomNavigation(navController)
                        }
                    },
                    containerColor = Background,
                    modifier = Modifier.fillMaxSize()
                ){ innerPadding ->
                    NavigationApp(
                        authViewModel = authViewModel,
                        navController = navController,
                        onAddToCart = {cartItem ->
                            cartViewModel.addItemToCart(cartItem)
                        },
                        modifier = Modifier.padding(
                            // Only apply padding if bars are showing
                            top = if (shouldShowTopBar) innerPadding.calculateTopPadding() else 0.dp,
                            bottom = if (shouldShowBottomBar) innerPadding.calculateBottomPadding() else 0.dp,
                            start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                            end = innerPadding.calculateEndPadding(LayoutDirection.Ltr)

                        ))
                }
            }
        }
    }
}

@Composable
fun JoMaloneHeader(navController: NavHostController) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars),
        color = Background
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo
            Text(
                text = "JO MALONE",
                fontFamily = Cormorant,
                fontSize = 28.sp,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
            )

            Text(
                text = "LONDON",
                fontFamily = Cormorant,
                fontSize = 24.sp,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
            )

            // Navigation Icons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Menu,
                    contentDescription = "Menu",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { }
                )

                Icon(
                    Icons.Default.Search,
                    contentDescription = "Search",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { }
                )

                IconButton(
                    onClick =
                        { navController.navigate("ShoppingCart") }
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingBag,
                        contentDescription = "Shopping Cart",
                        tint = Color.Black // Consider MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

// Updated MainActivity.kt - BottomNavigation function
@Composable
fun BottomNavigation(navController: NavHostController) {
    val currentRoute = navController.currentBackStackEntry?.destination?.route

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .background(Background),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            BottomNavItem(
                icon = Icons.Default.Home,
                label = "HOME",
                isActive = currentRoute == "HomePage",
                onClick = {
                    navController.navigate("HomePage") {
                        // Clear back stack to avoid multiple instances
                        popUpTo("HomePage") { inclusive = true }
                    }
                }
            )

            BottomNavItem(
                icon = Icons.Default.Build, // Using Build icon for customize
                label = "CUSTOMIZE",
                isActive = currentRoute == "PerfumeCustomization",
                onClick = {
                    navController.navigate("PerfumeCustomization") {
                        launchSingleTop = true
                    }
                }
            )

            BottomNavItem(
                icon = Icons.Default.Science, // Using Science icon for scent test
                label = "SCENT TEST",
                isActive = currentRoute == "ScentTest",
                onClick = {
                    navController.navigate("ScentTest") {
                        launchSingleTop = true
                    }
                }
            )

            BottomNavItem(
                icon = Icons.Default.Person,
                label = "PROFILE",
                isActive = currentRoute == "Profile",
                onClick = {
                    navController.navigate("Profile") {
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}



@Composable
fun BottomNavItem(
    icon: ImageVector,
    label: String,
    isActive: Boolean = false,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Icon(
            icon,
            contentDescription = label,
            modifier = Modifier.size(24.dp),
            tint = if (isActive) Color(0xFF8B4513) else Color.Gray
        )
        Text(
            text = label,
            fontSize = 10.sp,
            color = if (isActive) Color(0xFF8B4513) else Color.Gray
        )
        if (isActive) {
            Box(
                modifier = Modifier
                    .width(20.dp)
                    .height(2.dp)
                    .background(Color(0xFF8B4513))
            )
        }
    }
}
