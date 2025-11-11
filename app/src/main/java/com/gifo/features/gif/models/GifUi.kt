package com.gifo.features.gif.models

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import kotlinx.parcelize.Parcelize

@Immutable
@Parcelize
internal data class GifUi(
    val id: String,
    val title: String?,
    val url: String,
    val gifUrl: String,
    val importDatetime: String,
    val username: String?,
) : Parcelable
