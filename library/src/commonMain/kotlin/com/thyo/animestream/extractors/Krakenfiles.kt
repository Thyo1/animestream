package com.thyo.animestream.extractors

import com.thyo.animestream.SubtitleFile
import com.thyo.animestream.app
import com.thyo.animestream.utils.ExtractorApi
import com.thyo.animestream.utils.ExtractorLink
import com.thyo.animestream.utils.Qualities
import com.thyo.animestream.utils.httpsify
import com.thyo.animestream.utils.newExtractorLink

open class Krakenfiles : ExtractorApi() {
    override val name = "Krakenfiles"
    override val mainUrl = "https://krakenfiles.com"
    override val requiresReferer = false

    override suspend fun getUrl(
        url: String,
        referer: String?,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ) {
        val id = Regex("/(?:view|embed-video)/([\\da-zA-Z]+)").find(url)?.groupValues?.get(1)
        val doc = app.get("$mainUrl/embed-video/$id").document
        val link = doc.selectFirst("source")?.attr("src")

        callback.invoke(
            newExtractorLink(
                this.name,
                this.name,
                httpsify(link ?: return),
            )
        )

    }

}