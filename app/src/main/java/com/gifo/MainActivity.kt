package com.gifo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.gifo.design.theme.GifoTheme
import com.gifo.features.gif.details.GifDetailsScreen
import com.gifo.features.gif.gifs.GifsScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GifoTheme {
                Surface {
                    val backStack = rememberSaveable { mutableStateListOf<AppNavigation>(AppNavigation.Gifs) }

                    NavDisplay(
                        backStack = backStack,
                        modifier = Modifier.systemBarsPadding(),
                        entryDecorators = listOf(
                            rememberSaveableStateHolderNavEntryDecorator(),
                            rememberViewModelStoreNavEntryDecorator()
                        ),
                        onBack = { backStack.removeLastOrNull() },
                        entryProvider = { key ->
                            when (key) {
                                is AppNavigation.GifDetails -> NavEntry(key) {
                                    GifDetailsScreen(
                                        key.gifUi,
                                        onBack = { backStack.removeLastOrNull() })
                                }

                                AppNavigation.Gifs -> NavEntry(key) { GifsScreen({ backStack.add(AppNavigation.GifDetails(it)) }) }
                            }
                        }
                    )
                }
            }
        }
    }
}
