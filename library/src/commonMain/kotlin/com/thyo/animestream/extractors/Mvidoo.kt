package com.thyo.animestream.extractors

import com.thyo.animestream.SubtitleFile
import com.thyo.animestream.app
import com.thyo.animestream.utils.ExtractorApi
import com.thyo.animestream.utils.ExtractorLink
import com.thyo.animestream.utils.Qualities
import com.thyo.animestream.utils.newExtractorLink

open class Mvidoo : ExtractorApi() {
    override val name = "Mvidoo"
    override val mainUrl = "https://mvidoo.com"
    override val requiresReferer = true

    private fun String.decodeHex(): String {
        require(length % 2 == 0) { "Must have an even length" }
        return String(
            chunked(2)
                .map { it.toInt(16).toByte() }
                .toByteArray()
        )
    }

    override suspend fun getUrl(
        url: String,
        referer: String?,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ) {
        val document = app.get(url, referer = referer).text
        val data = Regex("""\{var\s*[^\s]+\s*=\s*(\[[^]]+])""").find(document)?.groupValues?.get(1)
            ?.removeSurrounding("[", "]")?.replace("\"", "")?.replace("\\x", "")?.split(",")?.map { it.decodeHex() }?.reversed()?.joinToString("") ?: return
        Regex("source\\s*src=\"([^\"]+)").find(data)?.groupValues?.get(1)?.let { link ->
            callback.invoke(
                newExtractorLink(
                    this.name,
                    this.name,
                    link
                ) {
                    this.referer = "$mainUrl/"
                    this.quality = Qualities.Unknown.value
                    this.headers = mapOf(
                        "Range" to "bytes=0-"
                    )
                }
            )
        }
    }
}