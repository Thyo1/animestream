package com.thyo.animestream.ui.player.source_priority

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thyo.animestream.databinding.PlayerPrioritizeItemBinding
import com.thyo.animestream.utils.AppContextUtils

data class SourcePriority<T>(
    val data: T,
    val name: String,
    var priority: Int
)

class PriorityAdapter<T>(override val items: MutableList<SourcePriority<T>>) :
    AppContextUtils.DiffAdapter<SourcePriority<T>>(items) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return PriorityViewHolder(
            PlayerPrioritizeItemBinding.inflate(LayoutInflater.from(parent.context),parent,false),
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is PriorityViewHolder -> holder.bind(items[position])
        }
    }

    class PriorityViewHolder(
        val binding: PlayerPrioritizeItemBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun <T> bind(item: SourcePriority<T>) {
            binding.priorityText.text = item.name

            fun updatePriority() {
                binding.priorityNumber.text = item.priority.toString()
            }

            updatePriority()
            binding.addButton.setOnClickListener {
                // If someone clicks til the integer limit then they deserve to crash.
                item.priority++
                updatePriority()
            }

            binding.subtractButton.setOnClickListener {
                item.priority--
                updatePriority()
            }
        }
    }
}