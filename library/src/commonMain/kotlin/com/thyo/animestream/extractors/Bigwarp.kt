package com.thyo.animestream.extractors

import com.thyo.animestream.SubtitleFile
import com.thyo.animestream.app
import com.thyo.animestream.utils.ExtractorApi
import com.thyo.animestream.utils.ExtractorLink
import com.thyo.animestream.utils.Qualities
import com.thyo.animestream.utils.newExtractorLink

open class BigwarpIO : ExtractorApi() {
    override var name = "Bigwarp"
    override var mainUrl = "https://bigwarp.io"
    override val requiresReferer = false

    private val sourceRegex = Regex("""file:\s*['"](.*?)['"],label:\s*['"](.*?)['"]""")
    private val qualityRegex = Regex("""\d+x(\d+) .*""")

    override suspend fun getUrl(
        url: String,
        referer: String?,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ) {
        val resp = app.get(url).text

        for (sourceMatch in sourceRegex.findAll(resp)) {
            val label = sourceMatch.groupValues[2]

            callback.invoke(
                newExtractorLink(
                    name,
                    "$name ${label.split(" ", limit = 2).getOrNull(1)}",
                    sourceMatch.groupValues[1], // streams are usually in mp4 format
                ) {
                    this.referer = url
                    this.quality =
                        qualityRegex.find(label)?.groupValues?.getOrNull(1)?.toIntOrNull()
                            ?: Qualities.Unknown.value
                }
            )
        }
    }
}

class BgwpCC : BigwarpIO() {
    override var mainUrl = "https://bgwp.cc"
}

class BigwarpArt : BigwarpIO() {
    override var mainUrl = "https://bigwarp.art"
}
