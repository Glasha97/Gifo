package com.gifo.data.gif.api

internal data class Gif(
    val id: String,
    val bitlyGifUrl: String,
    val bitlyUrl: String,
    val contentUrl: String,
    val embedUrl: String,
    val image: Image,
    val importDatetime: String,
    val isSticker: Int,
    val rating: String,
    val slug: String,
    val source: String?,
    val sourcePostUrl: String,
    val sourceTld: String,
    val title: String?,
    val trendingDatetime: String,
    val type: String,
    val url: String,
    val username: String?,
) {
    data class Image(
        val frames: String,
        val hash: String,
        val height: String,
        val mp4: String,
        val mp4Size: String,
        val size: String,
        val url: String,
        val webp: String,
        val webpSize: String,
        val width: String,
    )
}