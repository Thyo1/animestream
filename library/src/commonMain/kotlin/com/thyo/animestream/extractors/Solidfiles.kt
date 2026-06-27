package com.thyo.animestream.extractors

import com.fasterxml.jackson.annotation.JsonProperty
import com.thyo.animestream.app
import com.thyo.animestream.utils.AppUtils.tryParseJson
import com.thyo.animestream.utils.ExtractorApi
import com.thyo.animestream.utils.ExtractorLink
import com.thyo.animestream.utils.getQualityFromName
import com.thyo.animestream.utils.newExtractorLink


open class Solidfiles : ExtractorApi() {
    override val name = "Solidfiles"
    override val mainUrl = "https://www.solidfiles.com"
    override val requiresReferer = false

    override suspend fun getUrl(url: String, referer: String?): List<ExtractorLink> {
        val sources = mutableListOf<ExtractorLink>()
        with(app.get(url).document) {
            this.select("script").map { script ->
                if (script.data().contains("\"streamUrl\":")) {
                    val data = script.data().substringAfter("constant('viewerOptions', {").substringBefore("});")
                    val source = tryParseJson<ResponseSource>("{$data}")
                    val quality = Regex("\\d{3,4}p").find(source!!.nodeName)?.groupValues?.get(0)
                    sources.add(
                        newExtractorLink(
                            name,
                            name,
                            source.streamUrl,
                        ) {
                            this.referer = url
                            this.quality = getQualityFromName(quality)
                        }
                    )
                }
            }
        }
        return sources
    }


    private data class ResponseSource(
        @JsonProperty("streamUrl") val streamUrl: String,
        @JsonProperty("nodeName") val nodeName: String
    )

}