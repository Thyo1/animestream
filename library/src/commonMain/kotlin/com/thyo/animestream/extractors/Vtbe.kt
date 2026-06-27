package com.thyo.animestream.extractors

import com.fasterxml.jackson.annotation.JsonProperty
import com.thyo.animestream.app
import com.thyo.animestream.utils.*
import com.thyo.animestream.utils.AppUtils.tryParseJson
import com.thyo.animestream.utils.JsUnpacker
import com.thyo.animestream.utils.ExtractorApi
import com.thyo.animestream.utils.ExtractorLink
import com.thyo.animestream.utils.Qualities
import com.thyo.animestream.utils.getQualityFromName
import java.net.URI


open class Vtbe : ExtractorApi() {
    override var name = "Vtbe"
    override var mainUrl = "https://vtbe.to"
    override val requiresReferer = true

    override suspend fun getUrl(url: String, referer: String?): List<ExtractorLink>? {
        val response = app.get(url,referer=mainUrl).document
        val extractedpack =response.selectFirst("script:containsData(function(p,a,c,k,e,d))")?.data().toString()
            JsUnpacker(extractedpack).unpack()?.let { unPacked ->
                Regex("sources:\\[\\{file:\"(.*?)\"").find(unPacked)?.groupValues?.get(1)?.let { link ->
                    return listOf(
                        newExtractorLink(
                            this.name,
                            this.name,
                            link,
                        ) {
                            this.referer = referer ?: ""
                            this.quality = Qualities.Unknown.value
                        }
                    )
                }
            }
            return null
    }
}
