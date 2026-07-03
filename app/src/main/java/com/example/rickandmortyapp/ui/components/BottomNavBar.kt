package com.example.rickandmortyapp.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Airplay
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.outlined.Airplay
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Tv
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import com.example.rickandmortyapp.R
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
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val route: String,
    val label: String
)

@Composable
fun BottomNavBar(
    selectedRoute: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
            BottomNavBarItem(
                Icons.Default.Home,
                Icons.Outlined.Home,
                AppRoutes.HOME_SCREEN,
                stringResource(R.string.nav_home)
            ),
            BottomNavBarItem(
                Icons.Default.Tv,
                Icons.Outlined.Tv,
                AppRoutes.EPISODES_SCREEN,
                stringResource(R.string.nav_episodes)
            ),
            BottomNavBarItem(
                Icons.Default.Favorite,
                Icons.Default.FavoriteBorder,
                AppRoutes.FAV_SCREEN,
                stringResource(R.string.nav_favorites)
            ),
            BottomNavBarItem(
                Icons.Default.Search,
                Icons.Outlined.Search,
                AppRoutes.SEARCH_SCREEN,
                stringResource(R.string.nav_search)
            ),
            BottomNavBarItem(
                Icons.Default.Person,
                Icons.Outlined.Person,
                AppRoutes.PROFILE_SCREEN,
                stringResource(R.string.nav_profile)
            )
    )

    NavigationBar(
        modifier = Modifier.clip(
            RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        items.forEach { item ->
            val isSelected = selectedRoute == item.route || selectedRoute.startsWith(item.route)

            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.label
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        fontSize = if (isSelected) 12.sp else 11.sp,
                        maxLines = 1,
                        softWrap = false,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                selected = isSelected,
                onClick = { onNavigate(item.route) },
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
    AppTheme {
        BottomNavBar(
            selectedRoute = AppRoutes.HOME_SCREEN,
            onNavigate = {}
        )
    }
}