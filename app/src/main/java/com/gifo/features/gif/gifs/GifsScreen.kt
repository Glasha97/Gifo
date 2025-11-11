package com.gifo.features.gif.gifs

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.gifo.R
import com.gifo.common.compose.utils.getActivityOrNull
import com.gifo.common.core.BaseComposeScreen
import com.gifo.common.preview.PreviewContainer
import com.gifo.common.resource.StringResource
import com.gifo.features.gif.models.GifCategory
import com.gifo.features.gif.models.GifUi
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun GifsScreen(onGifClick: (GifUi) -> Unit) {
    val vm = koinViewModel<GifsViewModel>()
    val state by vm.state.collectAsStateWithLifecycle()

    val globalLoader by vm.progressVisibility.collectAsStateWithLifecycle()
    val activity = LocalContext.current.getActivityOrNull()

    BackHandler {
        if (state.isSearchMode) {
            vm.toggleSearchMode(false)
        } else {
            activity?.finish()
        }
    }

    BaseComposeScreen(vm = vm) {
        Content(
            state = state,
            globalLoader = globalLoader,
            onQueryChange = vm::onQueryChanged,
            toggleSearchMode = vm::toggleSearchMode,
            loadMoreSearchGif = vm::loadMoreSearchGifs,
            loadMoreTrendingGif = vm::loadMoreGifsByCategory,
            onCategoryClick = vm::onCategoryClick,
            onGifClick = onGifClick,
        )
    }
}

@Composable
private fun Content(
    state: GifsScreenState,
    globalLoader: Boolean,
    modifier: Modifier = Modifier,
    onQueryChange: (String) -> Unit,
    toggleSearchMode: (Boolean) -> Unit,
    loadMoreSearchGif: () -> Unit,
    loadMoreTrendingGif: () -> Unit,
    onCategoryClick: (GifCategory) -> Unit,
    onGifClick: (GifUi) -> Unit,
) {
    Column(modifier = modifier.padding(top = 12.dp)) {
        Header(state, onQueryChange, toggleSearchMode, onCategoryClick)

        Spacer(modifier = Modifier.height(16.dp))

        ListContent(
            state = state,
            loadMoreSearchGif = loadMoreSearchGif,
            loadMoreTrendingGif = loadMoreTrendingGif,
            onGifClick = onGifClick,
            globalLoader = globalLoader,
        )
    }
}

@Composable
private fun ColumnScope.Header(
    state: GifsScreenState,
    onQueryChange: (String) -> Unit,
    toggleSearchMode: (Boolean) -> Unit,
    onCategoryClick: (GifCategory) -> Unit
) {
    AnimatedContent(
        targetState = state.isSearchMode,
        modifier = Modifier.align(Alignment.End),
    ) { isSearchMode ->
        if (isSearchMode) {
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = onQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                textStyle = MaterialTheme.typography.bodyMedium,
                leadingIcon = {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(R.drawable.ic_search),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.inverseSurface
                    )
                },
                trailingIcon = {
                    val isVisible = remember(state.searchQuery) { derivedStateOf { state.searchQuery.isNotEmpty() } }

                    if (isVisible.value) {
                        Icon(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .clickable(onClick = { onQueryChange("") })
                                .padding(4.dp),
                            painter = painterResource(R.drawable.ic_remove),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.inverseSurface,
                        )
                    }
                },
                placeholder = {
                    Text(
                        text = stringResource(StringResource.search_gifs_placeholder),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                },
                maxLines = 1,
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.inverseSurface,
                    focusedBorderColor = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            )
        } else {
            Column {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(StringResource.app_name),
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyLarge,
                    )

                    Icon(
                        painter = painterResource(R.drawable.ic_search),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .size(32.dp)
                            .align(Alignment.CenterEnd)
                            .clip(CircleShape)
                            .clickable(onClick = { toggleSearchMode(true) })
                            .padding(2.dp),
                        tint = MaterialTheme.colorScheme.inverseSurface,
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                val gifCategories = remember { GifCategory.entries.toImmutableList() }

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(gifCategories, key = { it.ordinal }) { item ->
                        val isSelected = remember(state.selectedCategory, item) {
                            item == state.selectedCategory
                        }

                        Text(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { onCategoryClick(item) }
                                .background(
                                    color = if (isSelected) MaterialTheme.colorScheme.scrim else MaterialTheme.colorScheme.surface,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            text = stringResource(item.title),
                            color = if (isSelected) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.outline,
                            style = if (isSelected) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ColumnScope.ListContent(
    state: GifsScreenState,
    loadMoreSearchGif: () -> Unit,
    loadMoreTrendingGif: () -> Unit,
    onGifClick: (GifUi) -> Unit,
    globalLoader: Boolean
) {
    val items = remember(
        state.isSearchMode,
        state.searchGifs.items,
        state.gifsByCategory.items
    ) { if (state.isSearchMode) state.searchGifs.items else state.gifsByCategory.items }

    val listState = rememberLazyGridState()

    LaunchedEffect(listState, state.isSearchMode, state.searchGifs, state.gifsByCategory) {
        snapshotFlow {
            listState.layoutInfo.totalItemsCount > 0 &&
                    if (state.isSearchMode) {
                        state.searchGifs.isLoading.not()
                    } else {
                        state.gifsByCategory.isLoading.not()
                    } &&
                    if (state.isSearchMode) {
                        listState.layoutInfo.totalItemsCount < state.searchGifs.totalCount
                    } else {
                        listState.layoutInfo.totalItemsCount < state.gifsByCategory.totalCount
                    } &&
                    (listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1) >= listState.layoutInfo.totalItemsCount * 3 / 4
        }
            .distinctUntilChanged()
            .filter { it }
            .collect { if (state.isSearchMode) loadMoreSearchGif() else loadMoreTrendingGif() }
    }

    val configuration = LocalConfiguration.current

    val spanCount = when (configuration.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> {
            4
        }

        else -> {
            2
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(spanCount),
            modifier = Modifier.fillMaxSize(),
            state = listState,
            contentPadding = PaddingValues(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            content = {
                items(items, key = { it.id }) { item ->
                    var state: AsyncImagePainter.State? by remember { mutableStateOf(null) }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(4.dp))
                            .aspectRatio(1f),
                    ) {
                        if (state is AsyncImagePainter.State.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .size(32.dp)
                                    .align(Alignment.Center),
                                color = MaterialTheme.colorScheme.scrim,
                            )
                        }

                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(item.gifUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .clickable(onClick = { onGifClick(item) }),
                            contentScale = ContentScale.Crop,
                            onState = { state = it },
                        )
                    }
                }

                val isLoading = if (state.isSearchMode) {
                    state.searchGifs.isLoading
                } else {
                    state.gifsByCategory.isLoading
                }

                if (isLoading) {
                    item(
                        span = { GridItemSpan(spanCount) },
                        key = "Loader",
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .size(48.dp)
                                    .align(Alignment.Center),
                                color = MaterialTheme.colorScheme.scrim,
                            )
                        }
                    }
                }
            }
        )

        if (state.isSearchMode && state.searchGifs.items.isEmpty() && state.searchGifs.isLoading.not() && globalLoader.not()) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(R.drawable.search_state)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    modifier = Modifier
                        .size(128.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .aspectRatio(1f),
                    contentScale = ContentScale.Crop,
                )

                Spacer(modifier = Modifier.size(16.dp))

                Text(
                    text = stringResource(StringResource.search_start_typing),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@Preview
@Composable
private fun GifsScreenPreview() {
    PreviewContainer {
        Content(
            state = GifsScreenState(),
            globalLoader = false,
            modifier = Modifier.fillMaxSize(),
            onQueryChange = {},
            toggleSearchMode = {},
            loadMoreSearchGif = {},
            loadMoreTrendingGif = {},
            onCategoryClick = {},
            onGifClick = {},
        )
    }
}