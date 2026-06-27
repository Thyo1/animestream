package com.thyo.animestream.actions.temp

import android.content.Context
import com.thyo.animestream.actions.VideoClickAction
import com.thyo.animestream.ui.result.LinkLoadingResult
import com.thyo.animestream.ui.result.ResultEpisode
import com.thyo.animestream.utils.txt
import com.thyo.animestream.utils.UIHelper.clipboardHelper

class CopyClipboardAction: VideoClickAction() {
    override val name = txt("Copy to clipboard")

    override val oneSource = true

    override fun shouldShow(context: Context?, video: ResultEpisode?) = true

    override suspend fun runAction(
        context: Context?,
        video: ResultEpisode,
        result: LinkLoadingResult,
        index: Int?
    ) {
        if (index == null) return
        val link = result.links.getOrNull(index) ?: return
        clipboardHelper(txt(link.name), link.url)
    }
}