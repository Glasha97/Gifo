package com.gifo.features.gif.gifs

import androidx.compose.runtime.Immutable
import com.gifo.features.gif.models.GifCategory
import com.gifo.features.gif.models.GifUi
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
internal data class GifsScreenState(
    val isSearchMode: Boolean = false,
    val searchQuery: String = "",
    val gifsByCategory: Pagination = Pagination(),
    val searchGifs: Pagination = Pagination(),
    val selectedCategory: GifCategory = GifCategory.Trending,
) {

    @Immutable
    internal data class Pagination(
        val items: ImmutableList<GifUi> = persistentListOf(),
        val isLoading: Boolean = false,
        val offset: Int = 0,
        val totalCount: Int = 0,
    )
}
