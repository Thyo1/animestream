package com.thyo.animestream.extractors

import com.thyo.animestream.app
import com.thyo.animestream.utils.ExtractorApi
import com.thyo.animestream.utils.ExtractorLink
import com.thyo.animestream.utils.M3u8Helper


open class Jawcloud : ExtractorApi() {
    override var name = "Jawcloud"
    override var mainUrl = "https://jawcloud.co"
    override val requiresReferer = false

    override suspend fun getUrl(url: String, referer: String?): List<ExtractorLink>? {
        val doc = app.get(url).document
        val urlString = doc.select("html body div source").attr("src")
        val sources = mutableListOf<ExtractorLink>()
        if (urlString.contains("m3u8"))
            M3u8Helper.generateM3u8(
                name,
                urlString,
                url,
                headers = app.get(url).headers.toMap()
            ).forEach { link -> sources.add(link) }
        return sources
    }
}