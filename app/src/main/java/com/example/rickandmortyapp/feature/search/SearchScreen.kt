package com.example.rickandmortyapp.feature.search

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rickandmortyapp.R
import com.example.rickandmortyapp.data.model.CharacterStatus
import com.example.rickandmortyapp.feature.home.CharacterCard
import com.example.rickandmortyapp.ui.components.CustomTopBar
import com.example.rickandmortyapp.ui.theme.AppTheme
import com.example.rickandmortyapp.data.model.color
import com.example.rickandmortyapp.data.model.displayNameRes

@Composable
fun SearchScreen(
    viewModel: SearchViewModel = hiltViewModel(),
    onNavigateToCharacterDetails: (Int) -> Unit,
    onShowSnackbar: suspend (String) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is SearchEffect.ShowError -> onShowSnackbar(effect.message)
            }
        }
    }

    SearchScreenContent(
        state = state,
        onEvent = viewModel::onEvent,
        onCharacterClicked = onNavigateToCharacterDetails
    )
}

@Composable
fun SearchScreenContent(
    state: SearchState,
    onEvent: (SearchEvent) -> Unit,
    onCharacterClicked: (Int) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val gridState = rememberLazyGridState()

    val shouldLoadMore by remember {
        derivedStateOf {
            val totalItems = gridState.layoutInfo.totalItemsCount
            val lastVisible = gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            totalItems > 0 && lastVisible >= totalItems - 4
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore && !state.isLoading && !state.isLoadingMore && state.hasMorePages) {
            onEvent(SearchEvent.LoadNextPage)
        }
    }

    LaunchedEffect(gridState.isScrollInProgress) {
        if (gridState.isScrollInProgress) focusManager.clearFocus()
    }
    
    val isHeaderVisible by remember {
        derivedStateOf {
            gridState.firstVisibleItemIndex == 0 && gridState.firstVisibleItemScrollOffset == 0
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colorScheme.screenBackground)
    ) {
        CustomTopBar(stringResource(R.string.home_screen_top_bar_name))

        val searchTitle = stringResource(R.string.discover)
        val searchSubtitle = stringResource(R.string.search_subtitle)

        AnimatedVisibility(
            visible = isHeaderVisible,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            SearchHeader(
                title = searchTitle,
                subtitle = searchSubtitle
            )
        }

        PremiumSearchField(
            query = state.query,
            onQueryChange = { onEvent(SearchEvent.QueryChanged(it)) },
            onClear = { onEvent(SearchEvent.ClearSearch) },
            onSearchAction = {
                onEvent(SearchEvent.Search)
                focusManager.clearFocus()
            }
        )

        StatusFilterRow(
            selected = state.statusFilter,
            onStatusSelected = { onEvent(SearchEvent.StatusFilterChanged(it)) }
        )

        when {
            !state.hasSearched && state.query.isBlank() && state.statusFilter == null -> {
                SearchInitialState()
            }

            state.isLoading -> {
                SearchLoadingState()
            }

            state.error != null && state.results.isEmpty() -> {
                SearchErrorState(
                    message = state.error,
                    onRetry = { onEvent(SearchEvent.Retry) }
                )
            }

            state.hasSearched && state.results.isEmpty() -> {
                SearchNoResultsState()
            }

            else -> {
                SearchResultsGrid(
                    state = state,
                    gridState = gridState,
                    onCharacterClicked = onCharacterClicked,
                    onFavoriteClicked = { id, name ->
                        onEvent(
                            SearchEvent.FavoriteClicked(
                                id,
                                name
                            )
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun SearchHeader(title: String, subtitle: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 10.dp, bottom = 8.dp)
    ) {
        Text(
            text = title,
            style = AppTheme.typography.titleLarge,
            color = AppTheme.colorScheme.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = subtitle,
            style = AppTheme.typography.paragraph,
            color = AppTheme.colorScheme.primaryLight,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun PremiumSearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit,
    onSearchAction: () -> Unit
) {
    val fieldShape = RoundedCornerShape(18.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(fieldShape)
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        AppTheme.colorScheme.inputField,
                        AppTheme.colorScheme.surfaceVariant
                    )
                )
            )
            .border(
                width = 1.dp,
                color = AppTheme.colorScheme.border.copy(alpha = 0.65f),
                shape = fieldShape
            )
    ) {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            value = query,
            onValueChange = onQueryChange,
            placeholder = {
                Text(
                    text = stringResource(R.string.search_placeholder),
                    style = AppTheme.typography.paragraph,
                    color = AppTheme.colorScheme.textMuted
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = stringResource(R.string.search_icon_desc),
                    tint = AppTheme.colorScheme.secondary
                )
            },
            trailingIcon = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = onClear) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = stringResource(R.string.clear_search),
                                tint = AppTheme.colorScheme.iconSecondary
                            )
                        }
                    }
                }
            },
            singleLine = true,
            textStyle = AppTheme.typography.paragraph.copy(
                color = AppTheme.colorScheme.textPrimary,
                fontWeight = FontWeight.Medium
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { onSearchAction() }),
            colors = TextFieldDefaults.colors(
                focusedTextColor = AppTheme.colorScheme.textPrimary,
                unfocusedTextColor = AppTheme.colorScheme.textPrimary,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = AppTheme.colorScheme.secondary
            )
        )
    }
}

@Composable
private fun StatusFilterRow(
    selected: CharacterStatus?,
    onStatusSelected: (CharacterStatus?) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CharacterStatus.entries.forEach { status ->
            StatusChip(
                status = status,
                selected = selected == status,
                onClick = { onStatusSelected(if (selected == status) null else status) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun StatusChip(
    status: CharacterStatus,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(50)
    val background = if (selected) {
        Brush.horizontalGradient(
            colors = listOf(
                AppTheme.colorScheme.primaryLight,
                AppTheme.colorScheme.primary
            )
        )
    } else {
        Brush.horizontalGradient(
            colors = listOf(
                AppTheme.colorScheme.surface,
                AppTheme.colorScheme.surfaceVariant.copy(alpha = 0.72f)
            )
        )
    }

    Box(
        modifier = modifier
            .height(42.dp)
            .clip(shape)
            .background(background)
            .border(
                width = 1.dp,
                color = if (selected) AppTheme.colorScheme.primaryLight else AppTheme.colorScheme.border,
                shape = shape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(status.color)
            )
            Spacer(modifier = Modifier.size(7.dp))
            Text(
                text = stringResource(id = status.displayNameRes),
                style = AppTheme.typography.labelNormal,
                fontWeight = FontWeight.Bold,
                color = if (selected) AppTheme.colorScheme.onPrimary else AppTheme.colorScheme.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun SearchInitialState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.PersonSearch,
            contentDescription = null,
            modifier = Modifier.size(AppTheme.size.emptyIconSize),
            tint = AppTheme.colorScheme.primaryLight.copy(alpha = 0.9f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.find_any_character),
            style = AppTheme.typography.titleNormal,
            color = AppTheme.colorScheme.textPrimary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.search_hint),
            style = AppTheme.typography.paragraph,
            color = AppTheme.colorScheme.textMuted,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun SearchNoResultsState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.SearchOff,
            contentDescription = null,
            modifier = Modifier.size(AppTheme.size.emptyIconSize),
            tint = AppTheme.colorScheme.iconSecondary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.no_results_found),
            style = AppTheme.typography.titleNormal,
            color = AppTheme.colorScheme.textPrimary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.try_another_search),
            style = AppTheme.typography.paragraph,
            color = AppTheme.colorScheme.textMuted,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun SearchLoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = AppTheme.colorScheme.primary)
    }
}

@Composable
private fun SearchErrorState(
    message: String?,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.ErrorOutline,
            contentDescription = null,
            modifier = Modifier.size(AppTheme.size.emptyIconSize),
            tint = AppTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message ?: stringResource(R.string.something_went_wrong),
            style = AppTheme.typography.paragraph,
            color = AppTheme.colorScheme.textSecondary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = onRetry) {
            Text(
                text = stringResource(R.string.try_again),
                color = AppTheme.colorScheme.primaryLight,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun SearchResultsGrid(
    state: SearchState,
    gridState: LazyGridState,
    onCharacterClicked: (Int) -> Unit,
    onFavoriteClicked: (Int, String) -> Unit
) {
    LazyVerticalGrid(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colorScheme.screenBackground),
        columns = GridCells.Fixed(2),
        state = gridState,
        contentPadding = PaddingValues(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 28.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = state.results,
            key = { it.id }
        ) { character ->
            CharacterCard(
                character = character,
                isFavorite = character.id in state.favoriteIds,
                onClick = { onCharacterClicked(character.id) },
                onFavoriteClick = { onFavoriteClicked(character.id, character.name) }
            )
        }

        if (state.isLoadingMore) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = AppTheme.colorScheme.primary)
                }
            }
        }
    }
}