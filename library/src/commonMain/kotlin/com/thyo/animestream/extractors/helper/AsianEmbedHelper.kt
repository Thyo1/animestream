package com.thyo.animestream.extractors.helper

import com.thyo.api.Log
import com.thyo.animestream.SubtitleFile
import com.thyo.animestream.amap
import com.thyo.animestream.app
import com.thyo.animestream.utils.ExtractorLink
import com.thyo.animestream.utils.loadExtractor

class AsianEmbedHelper {
    companion object {
        suspend fun getUrls(
            url: String,
            subtitleCallback: (SubtitleFile) -> Unit,
            callback: (ExtractorLink) -> Unit
        ) {
            // Fetch links
            val doc = app.get(url).document
            val links = doc.select("div#list-server-more > ul > li.linkserver")
            if (!links.isNullOrEmpty()) {
                links.amap {
                    val datavid = it.attr("data-video") ?: ""
                    //Log.i("AsianEmbed", "Result => (datavid) ${datavid}")
                    if (datavid.isNotBlank()) {
                        val res = loadExtractor(datavid, url, subtitleCallback, callback)
                        Log.i("AsianEmbed", "Result => ($res) (datavid) $datavid")
                    }
                }
            }
        }
    }
}