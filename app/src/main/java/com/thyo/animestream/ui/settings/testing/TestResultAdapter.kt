package com.thyo.animestream.ui.settings.testing

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.thyo.animestream.CommonActivity.showToast
import com.thyo.animestream.MainAPI
import com.thyo.animestream.R
import com.thyo.animestream.databinding.ProviderTestItemBinding
import com.thyo.animestream.mvvm.getAllMessages
import com.thyo.animestream.mvvm.getStackTracePretty
import com.thyo.animestream.plugins.PluginManager
import com.thyo.animestream.utils.AppContextUtils
import com.thyo.animestream.utils.Coroutines.ioSafe
import com.thyo.animestream.utils.Coroutines.runOnMainThread
import com.thyo.animestream.utils.SubtitleHelper.getFlagFromIso
import com.thyo.animestream.utils.TestingUtils
import java.io.File

class TestResultAdapter(override val items: MutableList<Pair<MainAPI, TestingUtils.TestResultProvider>>) :
    AppContextUtils.DiffAdapter<Pair<MainAPI, TestingUtils.TestResultProvider>>(items) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ProviderTestViewHolder(
            ProviderTestItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            //LayoutInflater.from(parent.context)
            //    .inflate(R.layout.provider_test_item, parent, false),
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ProviderTestViewHolder -> {
                val item = items[position]
                holder.bind(item.first, item.second)
            }
        }
    }

    inner class ProviderTestViewHolder(binding: ProviderTestItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val languageText: TextView = binding.langIcon
        private val providerTitle: TextView = binding.mainText
        private val statusText: TextView = binding.passedFailedMarker
        private val failDescription: TextView = binding.failDescription
        private val logButton: ImageView = binding.actionButton

        private fun String.lastLine(): String? {
            return this.lines().lastOrNull { it.isNotBlank() }
        }

        fun bind(api: MainAPI, result: TestingUtils.TestResultProvider) {
            languageText.text = getFlagFromIso(api.lang)
            providerTitle.text = api.name

            val (resultText, resultColor) = if (result.success) {
                if (result.log.any { it.level == TestingUtils.Logger.LogLevel.Warning }) {
                    R.string.test_warning to R.color.colorTestWarning
                } else {
                    R.string.test_passed to R.color.colorTestPass
                }
            } else {
                R.string.test_failed to R.color.colorTestFail
            }

            statusText.setText(resultText)
            statusText.setTextColor(ContextCompat.getColor(itemView.context, resultColor))

            val stackTrace = result.exception?.getStackTracePretty(false)?.ifBlank { null }
            val messages = result.exception?.getAllMessages()?.ifBlank { null }
            val resultLog = result.log.joinToString("\n")
            val fullLog =
                resultLog +
                        (messages?.let { "\n\nError: $it" } ?: "") +
                        (stackTrace?.let { "\n\n$it" } ?: "")

            failDescription.text = messages?.lastLine() ?: resultLog.lastLine()

            logButton.setOnClickListener {
                val builder: AlertDialog.Builder =
                    AlertDialog.Builder(it.context, R.style.AlertDialogCustom)
                builder.setMessage(fullLog)
                    .setTitle(R.string.test_log)
                    // Ok button just closes the dialog
                    .setPositiveButton(R.string.ok) { _, _ -> }

                api.sourcePlugin?.let { path ->
                    val pluginFile = File(path)
                    // Cannot delete a deleted plugin
                    if (!pluginFile.exists()) return@let

                    builder.setNegativeButton(R.string.delete_plugin) { _, _ ->
                        ioSafe {
                            val success = PluginManager.deletePlugin(pluginFile)

                            runOnMainThread {
                                if (success) {
                                    showToast(R.string.plugin_deleted, Toast.LENGTH_SHORT)
                                } else {
                                    showToast(R.string.error, Toast.LENGTH_SHORT)
                                }
                            }
                        }
                    }
                }

                builder.show()
            }
        }
    }


}