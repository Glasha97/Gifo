package com.gifo.features.gif.details

import androidx.compose.runtime.Immutable
import com.gifo.features.gif.models.GifUi

@Immutable
internal data class GifDetailsScreenState(
    val gif: GifUi? = null,
)