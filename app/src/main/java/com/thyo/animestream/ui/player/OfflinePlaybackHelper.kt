package com.thyo.animestream.ui.player

import android.app.Activity
import android.content.ContentUris
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat.getString
import com.thyo.animestream.R
import com.thyo.animestream.actions.temp.AnimeStreamPackage
import com.thyo.animestream.utils.AppUtils.tryParseJson
import com.thyo.animestream.utils.DataStoreHelper
import com.thyo.animestream.utils.UIHelper.navigate
import com.lagradost.safefile.SafeFile

object OfflinePlaybackHelper {
    fun playLink(activity: Activity, url: String) {
        activity.navigate(
            R.id.global_to_navigation_player, GeneratorPlayer.newInstance(
                LinkGenerator(
                    listOf(
                        BasicLink(url)
                    )
                )
            )
        )
    }

    // See AnimeStreamPackage
    fun playIntent(activity: Activity, intent: Intent?): Boolean {
        if (intent == null) return false
        val links = intent.getStringArrayExtra(AnimeStreamPackage.LINKS_EXTRA)
            ?.mapNotNull { tryParseJson<AnimeStreamPackage.MinimalVideoLink>(it) } ?: emptyList()
        if (links.isEmpty()) return false
        val subs = intent.getStringArrayExtra(AnimeStreamPackage.SUBTITLE_EXTRA)
            ?.mapNotNull { tryParseJson<AnimeStreamPackage.MinimalSubtitleLink>(it) } ?: emptyList()

        val id = intent.getIntExtra(AnimeStreamPackage.ID_EXTRA, -1)
        //val title = intent.getStringExtra(AnimeStreamPackage.TITLE_EXTRA) // unused
        val pos = intent.getLongExtra(AnimeStreamPackage.POSITION_EXTRA, -1L)
        val dur = intent.getLongExtra(AnimeStreamPackage.DURATION_EXTRA, -1L)

        if (id != -1 && pos != -1L) {
            val duration = if (dur != -1L) {
                dur
            } else DataStoreHelper.getViewPos(id)?.duration ?: pos
            DataStoreHelper.setViewPos(id, pos, duration)
        }

        activity.navigate(
            R.id.global_to_navigation_player, GeneratorPlayer.newInstance(
                MinimalLinkGenerator(
                    links,
                    subs,
                    if (id != -1) id else null,
                )
            )
        )
        return true
    }

    fun playUri(activity: Activity, uri: Uri) {
        if (uri.scheme == "magnet") {
            playLink(activity, uri.toString())
            return
        }
        val name = SafeFile.fromUri(activity, uri)?.name()
        activity.navigate(
            R.id.global_to_navigation_player, GeneratorPlayer.newInstance(
                DownloadFileGenerator(
                    listOf(
                        ExtractorUri(
                            uri = uri,
                            name = name ?: getString(activity, R.string.downloaded_file),
                            // well not the same as a normal id, but we take it as users may want to
                            // play downloaded files and save the location
                            id = kotlin.runCatching { ContentUris.parseId(uri) }.getOrNull()
                                ?.hashCode()
                        )
                    )
                )
            )
        )
    }
}