package com.example.rickandmortyapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.example.rickandmortyapp.R
import com.example.rickandmortyapp.ui.components.AnimatedGradientText
import com.example.rickandmortyapp.ui.components.CustomBottomNavBar
import com.example.rickandmortyapp.ui.theme.AppTheme

@Preview(showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    AppTheme {
        HomeScreen()
    }
}

data class HomeCharacter(
    val name: String,
    val status: String,
    val image: Int
)

@Composable
fun HomeScreen() {

    var search by remember { mutableStateOf("") }

    val characters = listOf(
        HomeCharacter("Rick Sanchez", "Alive", R.drawable.ic_launcher_foreground),
        HomeCharacter("Morty Smith", "Alive", R.drawable.ic_launcher_foreground),
        HomeCharacter("Summer Smith", "Alive", R.drawable.ic_launcher_foreground),
        HomeCharacter("Beth Smith", "Alive", R.drawable.ic_launcher_foreground)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colorScheme.screenBackground)
    ) {

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = AppTheme.size.large),
            contentPadding = PaddingValues(
                top = AppTheme.size.large,
                bottom = AppTheme.size.bottomBarHeight + AppTheme.size.large
            )
        ) {

            item {
                AnimatedGradientText(
                    text = "Rick & Morty",
                    fontSize = AppTheme.typography.titleLarge.fontSize
                )

                Spacer(modifier = Modifier.height(AppTheme.size.medium))

                Text(
                    text = "Explore characters, episodes and locations across the multiverse.",
                    color = AppTheme.colorScheme.textSecondary,
                    style = AppTheme.typography.paragraph
                )

                Spacer(modifier = Modifier.height(AppTheme.size.large))

                SearchBar(
                    value = search,
                    onValueChange = { search = it }
                )

                Spacer(modifier = Modifier.height(AppTheme.size.large))

                SectionTitle(title = "Featured Characters")

                Spacer(modifier = Modifier.height(AppTheme.size.medium))
            }

            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.size.medium)
                ) {
                    items(characters) { character ->
                        CharacterCard(character = character)
                    }
                }

                Spacer(modifier = Modifier.height(AppTheme.size.large))

                SectionTitle(title = "Continue Exploring")

                Spacer(modifier = Modifier.height(AppTheme.size.medium))
            }

            items(characters) { character ->
                ContinueCard(character = character)

                Spacer(modifier = Modifier.height(AppTheme.size.medium))
            }
        }

        CustomBottomNavBar(
            selectedRoute = "home",
            onItemClick = {},
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun SearchBar(
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .height(AppTheme.size.searchHeight),
        placeholder = {
            Text(
                text = "Search multiverse...",
                color = AppTheme.colorScheme.textMuted,
                style = AppTheme.typography.labelNormal
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = null,
                tint = AppTheme.colorScheme.iconSecondary
            )
        },
        trailingIcon = {
            Icon(
                imageVector = Icons.Outlined.FilterList,
                contentDescription = null,
                tint = AppTheme.colorScheme.primary
            )
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text
        ),
        shape = AppTheme.shape.button,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = AppTheme.colorScheme.inputField,
            unfocusedContainerColor = AppTheme.colorScheme.inputField,
            focusedBorderColor = AppTheme.colorScheme.border,
            unfocusedBorderColor = AppTheme.colorScheme.border,
            focusedTextColor = AppTheme.colorScheme.textPrimary,
            unfocusedTextColor = AppTheme.colorScheme.textPrimary,
            cursorColor = AppTheme.colorScheme.primary
        )
    )
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        color = AppTheme.colorScheme.textPrimary,
        style = AppTheme.typography.titleNormal
    )
}

@Composable
private fun CharacterCard(character: HomeCharacter) {
    Card(
        modifier = Modifier
            .width(AppTheme.size.homeCardWidth)
            .height(AppTheme.size.homeCardHeight),
        shape = AppTheme.shape.container,
        colors = CardDefaults.cardColors(
            containerColor = AppTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(AppTheme.size.medium),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                painter = painterResource(id = character.image),
                contentDescription = character.name,
                modifier = Modifier
                    .size(AppTheme.size.characterImageSize)
                    .background(
                        color = AppTheme.colorScheme.inputField,
                        shape = CircleShape
                    ),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(AppTheme.size.medium))

            Text(
                text = character.name,
                color = AppTheme.colorScheme.textPrimary,
                style = AppTheme.typography.labelLarge
            )

            Spacer(modifier = Modifier.height(AppTheme.size.small))

            Text(
                text = character.status,
                color = AppTheme.colorScheme.success,
                style = AppTheme.typography.labelSmall
            )
        }
    }
}

@Composable
private fun ContinueCard(character: HomeCharacter) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = AppTheme.shape.container,
        colors = CardDefaults.cardColors(
            containerColor = AppTheme.colorScheme.surfaceContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppTheme.size.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Image(
                painter = painterResource(id = character.image),
                contentDescription = character.name,
                modifier = Modifier
                    .size(AppTheme.size.buttonHeight)
                    .background(
                        color = AppTheme.colorScheme.inputField,
                        shape = CircleShape
                    ),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(AppTheme.size.medium))

            Column {
                Text(
                    text = character.name,
                    color = AppTheme.colorScheme.textPrimary,
                    style = AppTheme.typography.labelLarge
                )

                Spacer(modifier = Modifier.height(AppTheme.size.small))

                Text(
                    text = "Character Profile",
                    color = AppTheme.colorScheme.textSecondary,
                    style = AppTheme.typography.labelSmall
                )
            }
        }
    }
}