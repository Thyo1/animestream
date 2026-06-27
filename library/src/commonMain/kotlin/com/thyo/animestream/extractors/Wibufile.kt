package com.thyo.animestream.extractors

import com.thyo.animestream.SubtitleFile
import com.thyo.animestream.app
import com.thyo.animestream.utils.ExtractorApi
import com.thyo.animestream.utils.ExtractorLink
import com.thyo.animestream.utils.INFER_TYPE
import com.thyo.animestream.utils.Qualities
import com.thyo.animestream.utils.newExtractorLink

open class Wibufile : ExtractorApi() {
    override val name: String = "Wibufile"
    override val mainUrl: String = "https://wibufile.com"
    override val requiresReferer = false

    override suspend fun getUrl(
        url: String,
        referer: String?,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ) {
        val res = app.get(url).text
        val video = Regex("src: ['\"](.*?)['\"]").find(res)?.groupValues?.get(1)

        callback.invoke(
            newExtractorLink(
                name,
                name,
                video ?: return,
            ) {
                this.referer = "$mainUrl/"
                this.quality = Qualities.Unknown.value
            }
        )
    }
}