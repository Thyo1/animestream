package com.thyo.animestream.ui.library

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.graphics.ColorUtils
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.thyo.animestream.databinding.SearchResultGridExpandedBinding
import com.thyo.animestream.syncproviders.SyncAPI
import com.thyo.animestream.ui.AutofitRecyclerView
import com.thyo.animestream.ui.search.SearchClickCallback
import com.thyo.animestream.ui.search.SearchResultBuilder
import com.thyo.animestream.utils.AppContextUtils
import com.thyo.animestream.utils.UIHelper.toPx
import kotlin.math.roundToInt


class PageAdapter(
    override val items: MutableList<SyncAPI.LibraryItem>,
    private val resView: AutofitRecyclerView,
    val clickCallback: (SearchClickCallback) -> Unit
) :
    AppContextUtils.DiffAdapter<SyncAPI.LibraryItem>(items) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return LibraryItemViewHolder(
            SearchResultGridExpandedBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is LibraryItemViewHolder -> {
                holder.bind(items[position], position)
            }
        }
    }

    private fun isDark(color: Int): Boolean {
        return ColorUtils.calculateLuminance(color) < 0.5
    }

    fun getDifferentColor(color: Int, ratio: Float = 0.7f): Int {
        return if (isDark(color)) {
            ColorUtils.blendARGB(color, Color.WHITE, ratio)
        } else {
            ColorUtils.blendARGB(color, Color.BLACK, ratio)
        }
    }

    inner class LibraryItemViewHolder(val binding: SearchResultGridExpandedBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val compactView = false//itemView.context.getGridIsCompact()
        private val coverHeight: Int =
            if (compactView) 80.toPx else (resView.itemWidth / 0.68).roundToInt()

        fun bind(item: SyncAPI.LibraryItem, position: Int) {
            /** https://stackoverflow.com/questions/8817522/how-to-get-color-code-of-image-view */

            SearchResultBuilder.bind(
                this@PageAdapter.clickCallback,
                item,
                position,
                itemView,
                /*colorCallback = { palette ->
                    AcraApplication.context?.let { ctx ->
                        val defColor = ContextCompat.getColor(ctx, R.color.ratingColorBg)
                        var bg = palette.getDarkVibrantColor(defColor)
                        if (bg == defColor) {
                            bg = palette.getDarkMutedColor(defColor)
                        }
                        if (bg == defColor) {
                            bg = palette.getVibrantColor(defColor)
                        }

                        val fg =
                            getDifferentColor(bg)//palette.getVibrantColor(ContextCompat.getColor(ctx,R.color.ratingColor))
                        binding.textRating.apply {
                            setTextColor(ColorStateList.valueOf(fg))
                        }
                        binding.textRating.compoundDrawables.getOrNull(0)?.setTint(fg)
                        binding.textRating.backgroundTintList = ColorStateList.valueOf(bg)
                        binding.watchProgress.apply {
                            progressTintList = ColorStateList.valueOf(fg)
                            progressBackgroundTintList = ColorStateList.valueOf(bg)
                        }
                    }
                }
                */
            )

            // See searchAdaptor for this, it basically fixes the height
            if (!compactView) {
                binding.imageView.apply {
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        coverHeight
                    )
                }
            }

            val showProgress = item.episodesCompleted != null && item.episodesTotal != null
            binding.watchProgress.isVisible = showProgress
            if (showProgress) {
                binding.watchProgress.max = item.episodesTotal!!
                binding.watchProgress.progress = item.episodesCompleted!!
            }

            binding.imageText.text = item.name
        }
    }
}