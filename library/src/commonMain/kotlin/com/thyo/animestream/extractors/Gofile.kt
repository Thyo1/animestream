package com.thyo.animestream.extractors

import com.fasterxml.jackson.annotation.JsonProperty
import com.thyo.animestream.SubtitleFile
import com.thyo.animestream.app
import com.thyo.animestream.utils.ExtractorApi
import com.thyo.animestream.utils.ExtractorLink
import com.thyo.animestream.utils.Qualities
import com.thyo.animestream.utils.newExtractorLink

open class Gofile : ExtractorApi() {
    override val name = "Gofile"
    override val mainUrl = "https://gofile.io"
    override val requiresReferer = false
    private val mainApi = "https://api.gofile.io"

    override suspend fun getUrl(
        url: String,
        referer: String?,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ) {
        val id = Regex("/(?:\\?c=|d/)([\\da-zA-Z-]+)").find(url)?.groupValues?.get(1)
        val token = app.get("$mainApi/createAccount").parsedSafe<Account>()?.data?.get("token")
        val websiteToken = app.get("$mainUrl/dist/js/alljs.js").text.let {
            Regex("fetchData.wt\\s*=\\s*\"([^\"]+)").find(it)?.groupValues?.get(1)
        }
        app.get("$mainApi/getContent?contentId=$id&token=$token&wt=$websiteToken")
            .parsedSafe<Source>()?.data?.contents?.forEach {
                callback.invoke(
                    newExtractorLink(
                        this.name,
                        this.name,
                        it.value["link"] ?: return,
                    ) {
                        this.quality = getQuality(it.value["name"])
                        this.headers = mapOf(
                            "Cookie" to "accountToken=$token"
                        )
                    }
                )
            }

    }

    private fun getQuality(str: String?): Int {
        return Regex("(\\d{3,4})[pP]").find(str ?: "")?.groupValues?.getOrNull(1)?.toIntOrNull()
            ?: Qualities.Unknown.value
    }

    data class Account(
        @JsonProperty("data") val data: HashMap<String, String>? = null,
    )

    data class Data(
        @JsonProperty("contents") val contents: HashMap<String, HashMap<String, String>>? = null,
    )

    data class Source(
        @JsonProperty("data") val data: Data? = null,
    )

}
