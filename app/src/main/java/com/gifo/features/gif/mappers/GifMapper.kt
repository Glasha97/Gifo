package com.gifo.features.gif.mappers

import com.gifo.data.gif.api.Gif
import com.gifo.features.gif.models.GifUi

internal fun Gif.toUi(): GifUi {
    return GifUi(
        id = id,
        url = url,
        title = title,
        gifUrl = image.url,
        importDatetime = importDatetime,
        username = username,
    )
}