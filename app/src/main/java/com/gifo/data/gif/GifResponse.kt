package com.gifo.data.gif

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GifResponse(
    @SerialName("bitly_gif_url")
    val bitlyGifUrl: String? = null,
    @SerialName("bitly_url")
    val bitlyUrl: String? = null,
    @SerialName("content_url")
    val contentUrl: String? = null,
    @SerialName("embed_url")
    val embedUrl: String? = null,
    @SerialName("id")
    val id: String? = null,
    @SerialName("images")
    val images: Images? = null,
    @SerialName("import_datetime")
    val importDatetime: String? = null,
    @SerialName("is_sticker")
    val isSticker: Int? = null,
    @SerialName("rating")
    val rating: String? = null,
    @SerialName("slug")
    val slug: String? = null,
    @SerialName("source")
    val source: String? = null,
    @SerialName("source_post_url")
    val sourcePostUrl: String? = null,
    @SerialName("source_tld")
    val sourceTld: String? = null,
    @SerialName("title")
    val title: String? = null,
    @SerialName("trending_datetime")
    val trendingDatetime: String? = null,
    @SerialName("type")
    val type: String? = null,
    @SerialName("url")
    val url: String? = null,
    @SerialName("username")
    val username: String? = null,
) {
    @Serializable
    data class Images(
        @SerialName("original")
        val original: Original? = null,
    ) {
        @Serializable
        data class Original(
            @SerialName("frames")
            val frames: String? = null,
            @SerialName("hash")
            val hash: String? = null,
            @SerialName("height")
            val height: String? = null,
            @SerialName("mp4")
            val mp4: String? = null,
            @SerialName("mp4_size")
            val mp4Size: String? = null,
            @SerialName("size")
            val size: String? = null,
            @SerialName("url")
            val url: String? = null,
            @SerialName("webp")
            val webp: String? = null,
            @SerialName("webp_size")
            val webpSize: String? = null,
            @SerialName("width")
            val width: String? = null,
        )
    }
}