package com.gifo.features.gif.gifs

import androidx.lifecycle.viewModelScope
import com.gifo.common.core.BaseVm
import com.gifo.common.models.domain.Pagination
import com.gifo.data.gif.api.Gif
import com.gifo.data.gif.api.GifRepository
import com.gifo.features.gif.mappers.toUi
import com.gifo.features.gif.models.GifCategory
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import org.koin.android.annotation.KoinViewModel

@OptIn(FlowPreview::class)
@KoinViewModel
internal class GifsViewModel(private val gifRepository: GifRepository) : BaseVm<GifsScreenState>(GifsScreenState()) {

    init {
        loadGifsByCategory()

        state.map { it.searchQuery }
            .distinctUntilChanged()
            .filter { it.isNotEmpty() }
            .debounce(300)
            .onEach { searchGifs(it) }
            .catch { sendError(it) }
            .launchIn(viewModelScope)
    }

    // region trending
    fun loadGifsByCategory() {
        startJob {
            val result = if (state.value.selectedCategory == GifCategory.Trending) {
                gifRepository.trending(pageSize = PAGE_SIZE)
            } else {
                val query = when (val category = state.value.selectedCategory) {
                    GifCategory.Trending -> null
                    GifCategory.Cats -> category.searchQuery
                    GifCategory.Love -> category.searchQuery
                    GifCategory.Funny -> category.searchQuery
                    GifCategory.Memes -> category.searchQuery
                    GifCategory.Movies -> category.searchQuery
                }

                gifRepository.search(query = query ?: return@startJob, pageSize = PAGE_SIZE)
            }

            handleGifsByCategoryResult(result)
        }
    }

    fun loadMoreGifsByCategory() {
        val state = state.value
        val offset = state.gifsByCategory.offset + PAGE_SIZE

        if (state.gifsByCategory.isLoading || offset >= state.gifsByCategory.totalCount) return

        startJob(withLoader = false) {
            update { s -> s.copy(gifsByCategory = s.gifsByCategory.copy(isLoading = true)) }

            val result = if (state.selectedCategory == GifCategory.Trending) {
                gifRepository.trending(offset = offset, pageSize = PAGE_SIZE)
            } else {
                val query = when (val category = state.selectedCategory) {
                    GifCategory.Trending -> null
                    GifCategory.Cats -> category.searchQuery
                    GifCategory.Love -> category.searchQuery
                    GifCategory.Funny -> category.searchQuery
                    GifCategory.Memes -> category.searchQuery
                    GifCategory.Movies -> category.searchQuery
                }

                gifRepository.search(query ?: return@startJob, offset = offset, pageSize = PAGE_SIZE)
            }

            handleGifsByCategoryResult(result)
        }
            .invokeOnCompletion { update { s -> s.copy(gifsByCategory = s.gifsByCategory.copy(isLoading = false)) } }
    }

    private fun handleGifsByCategoryResult(result: Pagination<Gif>) {
        val gifs = result.data.map { item -> item.toUi() }

        update { s ->
            s.copy(
                gifsByCategory = GifsScreenState.Pagination(
                    items = if (s.gifsByCategory.isLoading) (s.gifsByCategory.items + gifs).distinctBy { it.id }
                        .toImmutableList() else gifs.toImmutableList(),
                    isLoading = false,
                    offset = result.pagination.offset,
                    totalCount = result.pagination.totalCount,
                )
            )
        }
    }

    // endregion

    // region search
    fun searchGifs(value: String) {
        startJob {
            val result = gifRepository.search(query = value, pageSize = PAGE_SIZE)
            handleSearchResult(result)
        }
    }

    fun loadMoreSearchGifs() {
        val state = state.value
        val offset = state.searchGifs.offset + PAGE_SIZE

        if (state.searchGifs.isLoading || offset >= state.searchGifs.totalCount) return

        startJob(withLoader = false) {
            update { s -> s.copy(searchGifs = s.searchGifs.copy(isLoading = true)) }

            val result = gifRepository.search(query = state.searchQuery, offset = offset, pageSize = PAGE_SIZE)

            handleSearchResult(result)
        }.invokeOnCompletion { update { s -> s.copy(searchGifs = s.searchGifs.copy(isLoading = false)) } }
    }

    private fun handleSearchResult(result: Pagination<Gif>) {
        val gifs = result.data.map { item -> item.toUi() }

        update { s ->
            s.copy(
                searchGifs = GifsScreenState.Pagination(
                    items = if (s.searchGifs.isLoading) (s.searchGifs.items + gifs).distinctBy { it.id }
                        .toImmutableList() else gifs.toImmutableList(),
                    isLoading = false,
                    offset = result.pagination.offset,
                    totalCount = result.pagination.totalCount,
                )
            )
        }
    }

    // endregion

    fun onQueryChanged(query: String) {
        update { s ->
            val searchGifs = if (query.isEmpty()) {
                GifsScreenState.Pagination()
            } else {
                s.searchGifs
            }

            s.copy(
                searchQuery = query,
                searchGifs = searchGifs,
            )
        }
    }

    fun toggleSearchMode(value: Boolean) {
        update { s ->
            s.copy(
                isSearchMode = value,
                searchQuery = "",
                searchGifs = GifsScreenState.Pagination()
            )
        }
    }

    fun onCategoryClick(value: GifCategory) {
        update { s ->
            s.copy(
                selectedCategory = value,
                gifsByCategory = GifsScreenState.Pagination()
            )
        }

        loadGifsByCategory()
    }

    companion object {
        private const val PAGE_SIZE = 50
    }
}