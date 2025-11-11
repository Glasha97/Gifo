package com.gifo.data.gif

import com.gifo.data.gif.api.Gif
import com.gifo.data.gif.api.Gif.Image

internal fun GifResponse.toDomain(): Gif? {
    return Gif(
        id = id ?: return null,
        bitlyGifUrl = bitlyGifUrl ?: return null,
        bitlyUrl = bitlyUrl ?: return null,
        contentUrl = contentUrl ?: "",
        embedUrl = embedUrl ?: "",
        image = Image(
            frames = images?.original?.frames ?: "",
            hash = images?.original?.hash ?: "",
            height = images?.original?.height ?: "",
            mp4 = images?.original?.mp4 ?: "",
            mp4Size = images?.original?.mp4Size ?: "",
            size = images?.original?.size ?: "",
            url = images?.original?.url ?: "",
            webp = images?.original?.webp ?: "",
            webpSize = images?.original?.webpSize ?: "",
            width = images?.original?.width ?: "",
        ),
        importDatetime = importDatetime ?: "",
        isSticker = isSticker ?: 0,
        rating = rating ?: "",
        slug = slug ?: "",
        source = source ?: "",
        sourcePostUrl = sourcePostUrl ?: "",
        sourceTld = sourceTld ?: "",
        title = title ?: "",
        trendingDatetime = trendingDatetime ?: "",
        type = type ?: "",
        url = url ?: "",
        username = username ?: "",
    )
}