package com.gifo.features.gif.models

import androidx.compose.runtime.Immutable
import com.gifo.common.resource.StringResource

@Immutable
internal enum class GifCategory(val title: Int, val searchQuery: String?) {
    Trending(StringResource.trending, null),
    Cats(StringResource.cats, "cats"),
    Love(StringResource.love, "love"),
    Funny(StringResource.funny, "funny"),
    Memes(StringResource.memes, "memes"),
    Movies(StringResource.movies, "movies"),
}
