package com.gifo

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import com.gifo.features.gif.models.GifUi
import kotlinx.parcelize.Parcelize

@Immutable
@Parcelize
internal sealed interface AppNavigation : Parcelable {
    @Immutable
    data object Gifs : AppNavigation

    @Immutable
    data class GifDetails(val gifUi: GifUi) : AppNavigation

}