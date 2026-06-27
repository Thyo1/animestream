package com.thyo.animestream.extractors

import com.fasterxml.jackson.annotation.JsonProperty
import com.thyo.animestream.app
import com.thyo.animestream.utils.*
import com.thyo.animestream.utils.AppUtils.tryParseJson

open class EPlayExtractor : ExtractorApi() {
    override var name = "EPlay"
    override var mainUrl = "https://eplayvid.net"
    override val requiresReferer = true

    override suspend fun getUrl(url: String, referer: String?): List<ExtractorLink>? {
        val response = app.get(url).document
        val trueUrl = response.select("source").attr("src")
        return listOf(
                newExtractorLink(
                        this.name,
                        this.name,
                        trueUrl,
                ) {
                    this.referer = mainUrl
                    this.quality = getQualityFromName("") // this needs to be auto
                }
        )
    }
}
