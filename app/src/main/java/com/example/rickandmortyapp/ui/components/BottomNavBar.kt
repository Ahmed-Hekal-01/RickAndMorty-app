package com.example.rickandmortyapp.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Airplay
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rickandmortyapp.ui.theme.AppTheme
import com.example.rickandmortyapp.util.AppRoutes

private data class BottomNavBarItem(
    val icon: ImageVector,
    val route: String ,
    val label : String
)

@Composable
fun BottomNavBar(
    selectedRoute: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    val items = listOf(
        BottomNavBarItem(Icons.Default.Home, AppRoutes.HOME_SCREEN , label = "Home"),
        BottomNavBarItem(Icons.Default.Airplay, AppRoutes.EPISODES_SCREEN , label = "Episodes"),
        BottomNavBarItem(Icons.Default.FavoriteBorder, AppRoutes.FAV_SCREEN , label = "Favourite"),
        BottomNavBarItem(Icons.Default.Search, AppRoutes.SEARCH_SCREEN , label = "Search"),
        BottomNavBarItem(Icons.Default.Person, AppRoutes.PROFILE_SCREEN , label = "Profile")
    )
    NavigationBar(
        modifier = modifier
            .padding(start = 28.dp, end = 28.dp, bottom = 16.dp, top = 8.dp)
            .shadow(elevation = 10.dp, shape = RoundedCornerShape(24.dp))
            .clip(RoundedCornerShape(32.dp)),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        items.forEach { item ->
            val isSelected  = selectedRoute == item.route || selectedRoute.startsWith(item.route)

            NavigationBarItem(
                icon = { Icon(imageVector = item.icon , contentDescription = item.label) } ,
                label = {
                    Text(
                        text = item.label ,
                        fontSize = if (isSelected) 14.sp else 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                } ,
                selected = isSelected ,
                onClick = { onNavigate(item.route)} ,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

@Preview
@Composable
fun BottomNavBarPreview() {
    AppTheme{
        BottomNavBar(
            selectedRoute = AppRoutes.HOME_SCREEN ,
            onNavigate = {}
        )
    }
}