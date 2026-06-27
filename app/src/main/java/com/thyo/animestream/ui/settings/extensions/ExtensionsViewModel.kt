package com.thyo.animestream.ui.settings.extensions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fasterxml.jackson.annotation.JsonProperty
import com.thyo.animestream.AcraApplication.Companion.getKey
import com.thyo.animestream.R
import com.thyo.animestream.amap
import com.thyo.animestream.mvvm.debugAssert
import com.thyo.animestream.plugins.PluginManager
import com.thyo.animestream.plugins.PluginManager.getPluginsOnline
import com.thyo.animestream.plugins.RepositoryManager
import com.thyo.animestream.plugins.RepositoryManager.PREBUILT_REPOSITORIES
import com.thyo.animestream.utils.UiText
import com.thyo.animestream.utils.txt
import com.thyo.animestream.utils.Coroutines.ioSafe

data class RepositoryData(
    @JsonProperty("iconUrl") val iconUrl: String?,
    @JsonProperty("name") val name: String,
    @JsonProperty("url") val url: String
){
    constructor(name: String,url: String):this(null,name,url)
}

const val REPOSITORIES_KEY = "REPOSITORIES_KEY"

class ExtensionsViewModel : ViewModel() {
    data class PluginStats(
        val total: Int,

        val downloaded: Int,
        val disabled: Int,
        val notDownloaded: Int,

        val downloadedText: UiText,
        val disabledText: UiText,
        val notDownloadedText: UiText,
    )

    private val _repositories = MutableLiveData<Array<RepositoryData>>()
    val repositories: LiveData<Array<RepositoryData>> = _repositories

    private val _pluginStats: MutableLiveData<PluginStats?> = MutableLiveData(null)
    val pluginStats: LiveData<PluginStats?> = _pluginStats

    //TODO CACHE GET REQUESTS
    // DO not use viewModelScope.launchSafe, it will ANR on slow internet
    fun loadStats() = ioSafe {
        val urls = (getKey<Array<RepositoryData>>(REPOSITORIES_KEY)
            ?: emptyArray()) + PREBUILT_REPOSITORIES

        val onlinePlugins = urls.toList().amap {
            RepositoryManager.getRepoPlugins(it.url)?.toList() ?: emptyList()
        }.flatten().distinctBy { it.second.url }

        // Iterates over all offline plugins, compares to remote repo and returns the plugins which are outdated
        val outdatedPlugins = getPluginsOnline().map { savedData ->
            onlinePlugins.filter { onlineData -> savedData.internalName == onlineData.second.internalName }
                .map { onlineData ->
                    PluginManager.OnlinePluginData(savedData, onlineData)
                }
        }.flatten().distinctBy { it.onlineData.second.url }

        val total = onlinePlugins.count()
        val disabled = outdatedPlugins.count { it.isDisabled }
        val downloadedTotal = outdatedPlugins.count()
        val downloaded = downloadedTotal - disabled
        val notDownloaded = total - downloadedTotal
        val stats = PluginStats(
            total,
            downloaded,
            disabled,
            notDownloaded,
            txt(R.string.plugins_downloaded, downloaded),
            txt(R.string.plugins_disabled, disabled),
            txt(R.string.plugins_not_downloaded, notDownloaded)
        )
        debugAssert({ stats.downloaded + stats.notDownloaded + stats.disabled != stats.total }) {
            "downloaded(${stats.downloaded}) + notDownloaded(${stats.notDownloaded}) + disabled(${stats.disabled}) != total(${stats.total})"
        }
        _pluginStats.postValue(stats)
    }

    private fun repos() = (getKey<Array<RepositoryData>>(REPOSITORIES_KEY)
        ?: emptyArray()) + PREBUILT_REPOSITORIES

    fun loadRepositories() {
        val urls = repos()
        _repositories.postValue(urls)
    }
}