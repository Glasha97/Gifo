package com.gifo.features.gif.gifs

import com.gifo.MainDispatcherRule
import com.gifo.common.models.domain.Pagination
import com.gifo.common.models.domain.PaginationData
import com.gifo.data.gif.api.Gif
import com.gifo.data.gif.api.GifRepository
import com.gifo.features.gif.models.GifCategory
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GifsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK(relaxed = true)
    private lateinit var repository: GifRepository

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
    }

    @Test
    fun `init loads trending gifs with default page size`() = runTest {
        val expectedPagination = PaginationData(count = 2, offset = 0, totalCount = 100)
        val expectedGifs = listOf(createGif("1"), createGif("2"))

        coEvery { repository.trending(offset = 0, pageSize = 50) } returns pagination(expectedPagination, expectedGifs)

        val viewModel = GifsViewModel(repository)

        advanceUntilIdle()

        val state = viewModel.state.value
        coVerify(exactly = 1) { repository.trending(offset = 0, pageSize = 50) }
        assertEquals(2, state.gifsByCategory.items.size)
        assertEquals(0, state.gifsByCategory.offset)
        assertEquals(100, state.gifsByCategory.totalCount)
    }

    @Test
    fun `loadGifsByCategory uses search when category selected`() = runTest {
        coEvery { repository.trending(offset = 0, pageSize = 50) } returns pagination(
            PaginationData(count = 0, offset = 0, totalCount = 0),
            emptyList()
        )

        val categoryPage = pagination(
            PaginationData(count = 2, offset = 0, totalCount = 40),
            listOf(createGif("10"), createGif("11"))
        )

        val viewModel = GifsViewModel(repository)
        advanceUntilIdle()

        viewModel.update { it.copy(selectedCategory = GifCategory.Cats) }
        coEvery { repository.search(query = "cats", offset = 0, pageSize = 50) } returns categoryPage

        viewModel.loadGifsByCategory()
        advanceUntilIdle()

        coVerify { repository.search(query = "cats", offset = 0, pageSize = 50) }
        val state = viewModel.state.value
        assertEquals(GifCategory.Cats, state.selectedCategory)
        assertEquals(2, state.gifsByCategory.items.size)
        assertEquals(0, state.gifsByCategory.offset)
        assertEquals(40, state.gifsByCategory.totalCount)
    }

    @Test
    fun `loadMoreGifsByCategory appends data and requests next offset`() = runTest {
        val firstPage = pagination(
            PaginationData(count = 2, offset = 0, totalCount = 120),
            listOf(createGif("1"), createGif("2"))
        )
        val secondPage = pagination(
            PaginationData(count = 2, offset = 50, totalCount = 120),
            listOf(createGif("3"), createGif("4"))
        )

        coEvery { repository.trending(offset = 0, pageSize = 50) } returns firstPage
        coEvery { repository.trending(offset = 50, pageSize = 50) } returns secondPage

        val viewModel = GifsViewModel(repository)

        viewModel.loadMoreGifsByCategory()

        advanceUntilIdle()

        coVerify(exactly = 1) { repository.trending(offset = 0, pageSize = 50) }
        coVerify(exactly = 1) { repository.trending(offset = 50, pageSize = 50) }
        val state = viewModel.state.value
        assertEquals(4, state.gifsByCategory.items.size)
        assertTrue(state.gifsByCategory.items.any { it.id == "3" })
        assertEquals(50, state.gifsByCategory.offset)
    }

    @Test
    fun `loadMoreGifsByCategory stops when total count reached`() = runTest {
        val firstPage = pagination(
            PaginationData(count = 1, offset = 0, totalCount = 1),
            listOf(createGif("1"))
        )

        coEvery { repository.trending(offset = 0, pageSize = 50) } returns firstPage

        val viewModel = GifsViewModel(repository)

        advanceUntilIdle()

        viewModel.loadMoreGifsByCategory()
        advanceUntilIdle()

        coVerify(exactly = 1) { repository.trending(offset = 0, pageSize = 50) }
    }

    @Test
    fun `loadMoreSearchGifs appends results and keeps offset`() = runTest {
        val initialSearchPage = pagination(
            PaginationData(count = 1, offset = 0, totalCount = 60),
            listOf(createGif("1"))
        )
        val nextSearchPage = pagination(
            PaginationData(count = 1, offset = 50, totalCount = 60),
            listOf(createGif("2"))
        )

        coEvery { repository.trending(offset = 0, pageSize = 50) } returns pagination(
            PaginationData(count = 0, offset = 0, totalCount = 0),
            emptyList()
        )
        coEvery { repository.search(query = "cats", offset = 0, pageSize = 50) } returns initialSearchPage
        coEvery { repository.search(query = "cats", offset = 50, pageSize = 50) } returns nextSearchPage

        val viewModel = GifsViewModel(repository)
        viewModel.onQueryChanged("cats")
        viewModel.searchGifs("cats")
        advanceUntilIdle()

        viewModel.loadMoreSearchGifs()
        advanceUntilIdle()

        coVerify { repository.search(query = "cats", offset = 50, pageSize = 50) }
        val state = viewModel.state.value
        assertEquals(2, state.searchGifs.items.size)
        assertEquals(50, state.searchGifs.offset)
    }

    @Test
    fun `searchGifs loads results with provided query`() = runTest {
        val searchPage = pagination(
            PaginationData(count = 1, offset = 0, totalCount = 60),
            listOf(createGif("10"))
        )

        coEvery { repository.trending(offset = 0, pageSize = 50) } returns pagination(
            PaginationData(count = 0, offset = 0, totalCount = 0),
            emptyList()
        )
        coEvery { repository.search(query = "cats", offset = 0, pageSize = 50) } returns searchPage

        val viewModel = GifsViewModel(repository)
        viewModel.searchGifs("cats")

        advanceUntilIdle()

        coVerify { repository.search(query = "cats", offset = 0, pageSize = 50) }

        val state = viewModel.state.value
        assertEquals(1, state.searchGifs.items.size)
        assertEquals("10", state.searchGifs.items.first().id)
    }

    @Test
    fun `onQueryChanged clears search data when query empty`() = runTest {
        coEvery { repository.trending(offset = 0, pageSize = 50) } returns pagination(
            PaginationData(count = 0, offset = 0, totalCount = 0),
            emptyList()
        )

        val viewModel = GifsViewModel(repository)
        advanceUntilIdle()

        viewModel.update {
            it.copy(
                searchQuery = "cats",
                searchGifs = GifsScreenState.Pagination(isLoading = true, offset = 10, totalCount = 100)
            )
        }

        viewModel.onQueryChanged("")

        val state = viewModel.state.value
        assertEquals("", state.searchQuery)
        assertTrue(state.searchGifs.items.isEmpty())
        assertFalse(state.searchGifs.isLoading)
        assertEquals(0, state.searchGifs.offset)
        assertEquals(0, state.searchGifs.totalCount)
    }

    @Test
    fun `onQueryChanged keeps pagination when query not empty`() = runTest {
        coEvery { repository.trending(offset = 0, pageSize = 50) } returns pagination(
            PaginationData(count = 0, offset = 0, totalCount = 0),
            emptyList()
        )

        val viewModel = GifsViewModel(repository)
        advanceUntilIdle()

        viewModel.update {
            it.copy(
                searchGifs = GifsScreenState.Pagination(offset = 25, totalCount = 200)
            )
        }

        viewModel.onQueryChanged("cats")

        val state = viewModel.state.value
        assertEquals("cats", state.searchQuery)
        assertEquals(25, state.searchGifs.offset)
        assertEquals(200, state.searchGifs.totalCount)
    }

    @Test
    fun `toggleSearchMode resets search data`() = runTest {
        coEvery { repository.trending(offset = 0, pageSize = 50) } returns pagination(
            PaginationData(count = 0, offset = 0, totalCount = 0),
            emptyList()
        )

        val viewModel = GifsViewModel(repository)

        viewModel.update {
            it.copy(
                isSearchMode = true,
                searchQuery = "cats",
                searchGifs = GifsScreenState.Pagination(isLoading = true, offset = 10, totalCount = 30)
            )
        }

        viewModel.toggleSearchMode(false)

        val state = viewModel.state.value
        assertFalse(state.isSearchMode)
        assertEquals("", state.searchQuery)
        assertTrue(state.searchGifs.items.isEmpty())
        assertEquals(0, state.searchGifs.offset)
        assertEquals(0, state.searchGifs.totalCount)
    }

    @Test
    fun `onCategoryClick updates selection and reloads gifs`() = runTest {
        val trendingPage = pagination(
            PaginationData(count = 1, offset = 0, totalCount = 1),
            listOf(createGif("0"))
        )
        val categoryPage = pagination(
            PaginationData(count = 2, offset = 0, totalCount = 80),
            listOf(createGif("5"), createGif("6"))
        )

        coEvery { repository.trending(offset = 0, pageSize = 50) } returns trendingPage
        coEvery { repository.search(query = "cats", offset = 0, pageSize = 50) } returns categoryPage

        val viewModel = GifsViewModel(repository)

        viewModel.onCategoryClick(GifCategory.Cats)
        advanceUntilIdle()

        coVerify { repository.search(query = "cats", offset = 0, pageSize = 50) }
        val state = viewModel.state.value
        assertEquals(GifCategory.Cats, state.selectedCategory)
        assertEquals(2, state.gifsByCategory.items.size)
        assertEquals(0, state.gifsByCategory.offset)
        assertEquals(80, state.gifsByCategory.totalCount)
    }

    private fun pagination(paginationData: PaginationData, gifs: List<Gif>): Pagination<Gif> =
        Pagination(pagination = paginationData, data = gifs)

    private fun createGif(id: String): Gif {
        return Gif(
            id = id,
            bitlyGifUrl = "bitlyGifUrl$id",
            bitlyUrl = "bitlyUrl$id",
            contentUrl = "",
            embedUrl = "",
            image = Gif.Image(
                frames = "",
                hash = "",
                height = "100",
                mp4 = "",
                mp4Size = "",
                size = "",
                url = "https://example.com/$id.gif",
                webp = "",
                webpSize = "",
                width = "100"
            ),
            importDatetime = "2024-01-01",
            isSticker = 0,
            rating = "g",
            slug = "slug$id",
            source = null,
            sourcePostUrl = "",
            sourceTld = "",
            title = "title$id",
            trendingDatetime = "",
            type = "gif",
            url = "https://example.com/$id",
            username = null
        )
    }
}

