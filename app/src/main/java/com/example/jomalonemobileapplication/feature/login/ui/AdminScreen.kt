package com.example.jomalonemobileapplication.feature.login.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.jomalonemobileapplication.R


object AdminDestinations {
    const val ADMIN_MAIN_MENU = "admin_main_menu"
    const val USER_MANAGEMENT_SCREEN = "admin_user_management"
    const val PERFUME_STOCK_SCREEN = "admin_perfume_stock"
}


val CreamBeigeScreenBackground = Color(0xFFF7E7CE)
val TonalCreamButtonBackground = Color(0xFFEDE0D1)
val DarkerTextOnCream = Color(0xFF7A6A53)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(navController: NavController) {

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = CreamBeigeScreenBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Admin Main Menu",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = DarkerTextOnCream
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Call the version of StyledIconButton that takes an ImageVector
                StyledIconButton(
                    onClick = { navController.navigate(AdminDestinations.USER_MANAGEMENT_SCREEN) },
                    icon = Icons.Filled.Person, // Pass the ImageVector
                    text = "User",
                    contentDescription = "Manage Users",
                    buttonBackgroundColor = TonalCreamButtonBackground,
                    borderColor = DarkerTextOnCream,
                    iconTextColor = DarkerTextOnCream
                )

                Spacer(modifier = Modifier.width(24.dp))

                // Call the version of StyledIconButton that takes a Painter
                StyledIconButton(
                    onClick = { navController.navigate(AdminDestinations.PERFUME_STOCK_SCREEN) },
                    iconPainter = painterResource(id = R.drawable.perfume), // Use your perfume.png
                    text = "Perfume",
                    contentDescription = "Manage Perfume Stock",
                    buttonBackgroundColor = TonalCreamButtonBackground,
                    borderColor = DarkerTextOnCream,
                    iconTextColor = DarkerTextOnCream
                )
            }
        }
    }
}

// Version for ImageVector
@Composable
fun StyledIconButton(
    onClick: () -> Unit,
    icon: ImageVector, // Takes ImageVector
    text: String,
    contentDescription: String,
    modifier: Modifier = Modifier,
    buttonSize: Dp = 120.dp,
    cornerRadius: Dp = 32.dp,
    iconSize: Dp = 48.dp,
    buttonBackgroundColor: Color,
    borderColor: Color,
    iconTextColor: Color
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.size(buttonSize),
        shape = RoundedCornerShape(cornerRadius),
        border = BorderStroke(1.5.dp, borderColor),
        colors = ButtonDefaults.outlinedButtonColors(containerColor = buttonBackgroundColor),
        contentPadding = ButtonDefaults.TextButtonContentPadding
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize().padding(4.dp)
        ) {
            Icon(
                imageVector = icon, // Uses imageVector
                contentDescription = contentDescription,
                modifier = Modifier.size(iconSize),
                tint = iconTextColor
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = text,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                color = iconTextColor,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// Version for Painter (for your PNG)
@Composable
fun StyledIconButton(
    onClick: () -> Unit,
    iconPainter: Painter, // Takes Painter
    text: String,
    contentDescription: String,
    modifier: Modifier = Modifier,
    buttonSize: Dp = 120.dp,
    cornerRadius: Dp = 32.dp,
    iconSize: Dp = 48.dp,
    buttonBackgroundColor: Color,
    borderColor: Color,
    iconTextColor: Color
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.size(buttonSize),
        shape = RoundedCornerShape(cornerRadius),
        border = BorderStroke(1.5.dp, borderColor),
        colors = ButtonDefaults.outlinedButtonColors(containerColor = buttonBackgroundColor),
        contentPadding = ButtonDefaults.TextButtonContentPadding
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize().padding(4.dp)
        ) {
            Icon(
                painter = iconPainter, // Uses painter
                contentDescription = contentDescription,
                modifier = Modifier.size(iconSize),
                // For a multi-color PNG, you might not want a tint, or set to Color.Unspecified
                // If perfume.png is single color and you want to tint it, this is fine.
                tint = iconTextColor // Or Color.Unspecified
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = text,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                color = iconTextColor,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

