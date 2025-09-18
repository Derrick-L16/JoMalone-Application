//package com.example.jomalonemobileapplication
//
//import android.os.Bundle
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.activity.enableEdgeToEdge
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.layout.width
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Home
//import androidx.compose.material.icons.filled.Menu
//import androidx.compose.material.icons.filled.Person
//import androidx.compose.material.icons.filled.Search
//import androidx.compose.material3.Card
//import androidx.compose.material3.CardDefaults
//import androidx.compose.material3.Icon
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.vector.ImageVector
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.navigation.NavController
//import androidx.navigation.NavHostController
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.compose.rememberNavController
//import com.example.jomalonemobileapplication.core.theme.Background
//import com.example.jomalonemobileapplication.core.theme.JoMaloneMobileApplicationTheme
//import com.example.jomalonemobileapplication.core.ui.ShoppingCartRoute
//import com.example.jomalonemobileapplication.core.ui.ShoppingCartScreen
//
//class JoMaloneApplication : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContent {
//            JoMaloneMobileApplicationTheme {
//                val navController = rememberNavController()
//                Scaffold(
//                    topBar = { JoMaloneHeader() },
//                    bottomBar = { BottomNavigation(navController) },
//                    containerColor = Background
//                ) { innerPadding ->
//                    AppNavigation(
//                        navController = navController,
//                        modifier = Modifier.padding(innerPadding)
//                    )
//                }
//            }
//        }
//    }
//}
//
////@Composable
////fun JoMaloneHeader() {
////    Column(
////        modifier = Modifier.fillMaxWidth(),
////        horizontalAlignment = Alignment.CenterHorizontally
////    ) {
////        // Logo
////        Text(
////            text = "JO MALONE",
////            fontSize = 18.sp,
////            fontWeight = FontWeight.Bold,
////            letterSpacing = 2.sp
////        )
////        Text(
////            text = "LONDON",
////            fontSize = 12.sp,
////            letterSpacing = 1.sp,
////            color = Color.Gray
////        )
////
////        Spacer(modifier = Modifier.height(24.dp))
////
////        // Navigation Icons
////        Row(
////            modifier = Modifier
////                .fillMaxWidth()
////                .padding(horizontal = 16.dp),
////            horizontalArrangement = Arrangement.SpaceBetween,
////            verticalAlignment = Alignment.CenterVertically
////        ) {
////            Icon(
////                Icons.Default.Menu,
////                contentDescription = "Menu",
////                modifier = Modifier
////                    .size(24.dp)
////                    .clickable { }
////            )
////
////            Icon(
////                Icons.Default.Search,
////                contentDescription = "Search",
////                modifier = Modifier
////                    .size(24.dp)
////                    .clickable { }
////            )
////        }
////    }
////}
////
////@Composable
////fun BottomNavigation(navController: NavController) {
////    Card(
////        modifier = Modifier.fillMaxWidth(),
////        colors = CardDefaults.cardColors(containerColor = Color.White),
////        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
////    ) {
////        Row(
////            modifier = Modifier
////                .fillMaxWidth()
////                .padding(vertical = 12.dp),
////            horizontalArrangement = Arrangement.SpaceEvenly
////        ) {
//////            BottomNavItem(Icons.Default.Home, "HOME") {
//////                navController.navigate("home")
//////            }
//////            BottomNavItem(Icons.Default.Search, "CUSTOMIZE", isActive = true) {
//////                navController.navigate("customize")
//////            }
//////            BottomNavItem(Icons.Default.Search, "SCENT TEST") {
//////                navController.navigate("scentTest")
//////            }
//////            BottomNavItem(Icons.Default.Person, "PROFILE") {
//////                navController.navigate("profile")
//////            }
////        }
////    }
////}
////
////@Composable
////fun BottomNavItem(
////    icon: ImageVector,
////    label: String,
////    isActive: Boolean = false,
////    onClick: () -> Unit
////) {
////    Column(
////        horizontalAlignment = Alignment.CenterHorizontally,
////        modifier = Modifier.clickable { onClick() }
////    ) {
////        Icon(
////            icon,
////            contentDescription = label,
////            modifier = Modifier.size(24.dp),
////            tint = if (isActive) Color(0xFF8B4513) else Color.Gray
////        )
////        Text(
////            text = label,
////            fontSize = 10.sp,
////            color = if (isActive) Color(0xFF8B4513) else Color.Gray
////        )
////        if (isActive) {
////            Box(
////                modifier = Modifier
////                    .width(20.dp)
////                    .height(2.dp)
////                    .background(Color(0xFF8B4513))
////            )
////        }
////    }
////}
////
////@Composable
////fun AppNavigation(navController: NavHostController, modifier: Modifier = Modifier) {
////
////    NavHost(
////        navController = navController,
////        startDestination = "cart"
////    ) {
////        composable("cart") {
////            ShoppingCartRoute(
////                onNavigateToCheckout = { }
////            ) // Use the route, not the screen directly
////        }
////        composable("scentTest") {
////            ScentTestScreen()
////        }
////        composable("customize") {
////            CustomizationFlow()
////        }
////    }
////}