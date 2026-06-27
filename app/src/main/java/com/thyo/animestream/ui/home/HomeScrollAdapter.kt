package com.thyo.animestream.ui.home

import android.content.res.Configuration
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import com.thyo.animestream.LoadResponse
import com.thyo.animestream.databinding.HomeScrollViewBinding
import com.thyo.animestream.databinding.HomeScrollViewTvBinding
import com.thyo.animestream.ui.NoStateAdapter
import com.thyo.animestream.ui.ViewHolderState
import com.thyo.animestream.ui.settings.Globals.EMULATOR
import com.thyo.animestream.ui.settings.Globals.TV
import com.thyo.animestream.ui.settings.Globals.isLayout
import com.thyo.animestream.utils.ImageLoader.loadImage

class HomeScrollAdapter(
    fragment: Fragment
) : NoStateAdapter<LoadResponse>(fragment) {
    var hasMoreItems: Boolean = false

    override fun onCreateContent(parent: ViewGroup): ViewHolderState<Any> {
        val inflater = LayoutInflater.from(parent.context)
        val binding = if (isLayout(TV or EMULATOR)) {
            HomeScrollViewTvBinding.inflate(inflater, parent, false)
        } else {
            HomeScrollViewBinding.inflate(inflater, parent, false)
        }

        return ViewHolderState(binding)
    }

    override fun onBindContent(
        holder: ViewHolderState<Any>,
        item: LoadResponse,
        position: Int,
    ) {
        val binding = holder.view
        val itemView = holder.itemView
        val isHorizontal =
            binding is HomeScrollViewTvBinding || itemView.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

        val posterUrl =
            if (isHorizontal) item.backgroundPosterUrl ?: item.posterUrl else item.posterUrl
                ?: item.backgroundPosterUrl

        when (binding) {
            is HomeScrollViewBinding -> {
                binding.homeScrollPreview.loadImage(posterUrl)
                binding.homeScrollPreviewTags.apply {
                    text = item.tags?.joinToString(" • ") ?: ""
                    isGone = item.tags.isNullOrEmpty()
                    maxLines = 2
                }
                binding.homeScrollPreviewTitle.text = item.name
            }

            is HomeScrollViewTvBinding -> {
                binding.homeScrollPreview.loadImage(posterUrl)
            }
        }
    }
}