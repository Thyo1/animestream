package com.thyo.animestream.extractors

import com.thyo.api.Log
import com.thyo.animestream.app
import com.thyo.animestream.utils.ExtractorApi
import com.thyo.animestream.utils.ExtractorLink
import com.thyo.animestream.utils.ExtractorLinkType
import com.thyo.animestream.utils.Qualities
import com.thyo.animestream.utils.newExtractorLink

open class AStreamHub : ExtractorApi() {
    override val name = "AStreamHub"
    override val mainUrl = "https://astreamhub.com"
    override val requiresReferer = true

    override suspend fun getUrl(url: String, referer: String?): List<ExtractorLink> {
        val sources = mutableListOf<ExtractorLink>()
        app.get(url).document.selectFirst("body > script").let { script ->
            val text = script?.html() ?: ""
            Log.i("Dev", "text => $text")
            if (text.isNotBlank()) {
                val m3link = "(?<=file:)(.*)(?=,)".toRegex().find(text)
                    ?.groupValues?.get(0)?.trim()?.trim('"') ?: ""
                Log.i("Dev", "m3link => $m3link")
                if (m3link.isNotBlank()) {
                    sources.add(
                        newExtractorLink(
                            name = name,
                            source = name,
                            url = m3link,
                            type = ExtractorLinkType.M3U8
                        ) {
                            this.quality = Qualities.Unknown.value
                            this.referer = referer ?: url
                        }
                    )
                }
            }
        }
        return sources
    }

}