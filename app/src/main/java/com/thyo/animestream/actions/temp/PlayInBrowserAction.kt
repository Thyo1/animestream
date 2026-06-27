package com.thyo.animestream.actions.temp

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.thyo.animestream.R
import com.thyo.animestream.actions.VideoClickAction
import com.thyo.animestream.ui.result.LinkLoadingResult
import com.thyo.animestream.ui.result.ResultEpisode
import com.thyo.animestream.utils.txt
import com.thyo.animestream.utils.ExtractorLinkType

class PlayInBrowserAction: VideoClickAction() {
    override val name = txt(R.string.episode_action_play_in_format, "Browser")

    override val oneSource = true

    override val isPlayer = true

    override val sourceTypes: Set<ExtractorLinkType> = setOf(
        ExtractorLinkType.VIDEO,
        ExtractorLinkType.DASH,
        ExtractorLinkType.M3U8
    )

    override fun shouldShow(context: Context?, video: ResultEpisode?) = true

    override suspend fun runAction(
        context: Context?,
        video: ResultEpisode,
        result: LinkLoadingResult,
        index: Int?
    ) {
        val link = result.links.getOrNull(index ?: 0) ?: return
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(link.url)
        launch(i)
    }
}