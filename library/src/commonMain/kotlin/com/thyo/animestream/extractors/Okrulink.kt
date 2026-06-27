package com.thyo.animestream.extractors

import com.fasterxml.jackson.annotation.JsonProperty
import com.thyo.animestream.app
import com.thyo.animestream.utils.ExtractorApi
import com.thyo.animestream.utils.ExtractorLink
import com.thyo.animestream.utils.Qualities
import com.thyo.animestream.utils.newExtractorLink

data class Okrulinkdata (
    @JsonProperty("status" ) var status : String? = null,
    @JsonProperty("url"    ) var url    : String? = null
)

open class Okrulink: ExtractorApi() {
    override var mainUrl = "https://okru.link"
    override var name = "Okrulink"
    override val requiresReferer = false

    override suspend fun getUrl(url: String, referer: String?): List<ExtractorLink> {
        val sources = mutableListOf<ExtractorLink>()
        val key = url.substringAfter("html?t=")
        val request = app.post("https://apizz.okru.link/decoding", allowRedirects = false,
            data = mapOf("video" to key)
        ).parsedSafe<Okrulinkdata>()
        if (request?.url != null) {
            sources.add(
                newExtractorLink(
                    name,
                    name,
                    request.url!!
                )
            )
        }
        return sources
    }
}
