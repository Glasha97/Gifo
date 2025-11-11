package com.gifo.features.gif.details

import com.gifo.features.gif.models.GifUi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GifDetailsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state contains provided gif`() = runTest {
        val gif = GifUi(
            id = "id",
            title = "title",
            url = "https://example.com",
            gifUrl = "https://example.com/gif",
            importDatetime = "2024-01-01",
            username = "author"
        )

        val viewModel = GifDetailsViewModel(gif)

        assertEquals(gif, viewModel.state.value.gif)
    }
}

