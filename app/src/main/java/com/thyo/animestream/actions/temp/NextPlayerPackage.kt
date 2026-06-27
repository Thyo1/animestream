package com.thyo.animestream.actions.temp

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import com.thyo.animestream.actions.OpenInAppAction
import com.thyo.animestream.ui.result.LinkLoadingResult
import com.thyo.animestream.ui.result.ResultEpisode
import com.thyo.animestream.utils.ExtractorLinkType
import com.thyo.animestream.utils.txt

/** https://github.com/anilbeesetti/nextplayer */
class NextPlayerPackage : OpenInAppAction(
    appName = txt("NextPlayer"),
    packageName = "dev.anilbeesetti.nextplayer",
    intentClass = "dev.anilbeesetti.nextplayer.feature.player.PlayerActivity"
) {
    override val sourceTypes: Set<ExtractorLinkType> =
        setOf(ExtractorLinkType.VIDEO, ExtractorLinkType.M3U8, ExtractorLinkType.DASH)

    override val oneSource: Boolean = true

    override suspend fun putExtra(
        context: Context,
        intent: Intent,
        video: ResultEpisode,
        result: LinkLoadingResult,
        index: Int?
    ) {
        intent.data = result.links[index!!].url.toUri()
    }

    override fun onResult(activity: Activity, intent: Intent?) = Unit
}