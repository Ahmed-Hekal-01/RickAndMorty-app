package com.example.rickandmortyapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ViewModule
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.rickandmortyapp.ui.theme.AppTheme

data class BottomNavItem(
    val icon: ImageVector,
    val route: String
)

@Composable
fun CustomBottomNavBar(
    selectedRoute: String,
    onItemClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    val items = listOf(
        BottomNavItem(Icons.Outlined.ViewModule, "home"),
        BottomNavItem(Icons.Outlined.CalendarMonth, "episodes"),
        BottomNavItem(Icons.Outlined.FavoriteBorder, "favorites"),
        BottomNavItem(Icons.Outlined.Search, "search"),
        BottomNavItem(Icons.Outlined.PersonOutline, "profile")
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = AppTheme.size.medium,
                vertical = AppTheme.size.normal
            )
            .background(
                color = AppTheme.colorScheme.surfaceContainer,
                shape = AppTheme.shape.container
            )
            .height(AppTheme.size.bottomBarHeight),
        contentAlignment = Alignment.Center
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppTheme.size.medium),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            items.forEach { item ->

                val isSelected = item.route == selectedRoute

                Box(
                    modifier = Modifier
                        .size(AppTheme.size.navIconSize)
                        .background(
                            color =
                                if (isSelected)
                                    AppTheme.colorScheme.primary
                                else
                                    AppTheme.colorScheme.surfaceContainer,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    IconButton(
                        onClick = {
                            onItemClick(item.route)
                        }
                    ) {

                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.route,
                            tint =
                                if (isSelected)
                                    AppTheme.colorScheme.onPrimary
                                else
                                    AppTheme.colorScheme.inactiveIcon
                        )
                    }
                }
            }
        }
    }
}