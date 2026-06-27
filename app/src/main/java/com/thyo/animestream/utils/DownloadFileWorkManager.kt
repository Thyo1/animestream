package com.thyo.animestream.utils

import android.app.Notification
import android.content.Context
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
import android.os.Build.VERSION.SDK_INT
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.thyo.animestream.AcraApplication.Companion.removeKey
import com.thyo.animestream.mvvm.logError
import com.thyo.animestream.utils.Coroutines.main
import com.thyo.animestream.utils.DataStore.getKey
import com.thyo.animestream.utils.VideoDownloadManager.WORK_KEY_INFO
import com.thyo.animestream.utils.VideoDownloadManager.WORK_KEY_PACKAGE
import com.thyo.animestream.utils.VideoDownloadManager.downloadCheck
import com.thyo.animestream.utils.VideoDownloadManager.downloadEpisode
import com.thyo.animestream.utils.VideoDownloadManager.downloadFromResume
import com.thyo.animestream.utils.VideoDownloadManager.downloadStatusEvent
import com.thyo.animestream.utils.VideoDownloadManager.getDownloadResumePackage
import kotlinx.coroutines.delay

const val DOWNLOAD_CHECK = "DownloadCheck"

class DownloadFileWorkManager(val context: Context, private val workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val key = workerParams.inputData.getString("key")
        try {
            if (key == DOWNLOAD_CHECK) {
                downloadCheck(applicationContext, ::handleNotification)
            } else if (key != null) {
                val info =
                    applicationContext.getKey<VideoDownloadManager.DownloadInfo>(WORK_KEY_INFO, key)
                val pkg =
                    applicationContext.getKey<VideoDownloadManager.DownloadResumePackage>(
                        WORK_KEY_PACKAGE,
                        key
                    )

                if (info != null) {
                    getDownloadResumePackage(applicationContext, info.ep.id)?.let { dpkg ->
                        downloadFromResume(applicationContext, dpkg, ::handleNotification)
                    } ?: run {
                        downloadEpisode(
                            applicationContext,
                            info.source,
                            info.folder,
                            info.ep,
                            info.links,
                            ::handleNotification
                        )
                    }
                } else if (pkg != null) {
                    downloadFromResume(applicationContext, pkg, ::handleNotification)
                }
                removeKeys(key)
            }
            return Result.success()
        } catch (e: Exception) {
            logError(e)
            if (key != null) {
                removeKeys(key)
            }
            return Result.failure()
        }
    }

    private fun removeKeys(key: String) {
        removeKey(WORK_KEY_INFO, key)
        removeKey(WORK_KEY_PACKAGE, key)
    }

    private suspend fun awaitDownload(id: Int) {
        var isDone = false
        val listener = { (localId, localType): Pair<Int, VideoDownloadManager.DownloadType> ->
            if (id == localId) {
                when (localType) {
                    VideoDownloadManager.DownloadType.IsDone, VideoDownloadManager.DownloadType.IsFailed, VideoDownloadManager.DownloadType.IsStopped -> {
                        isDone = true
                    }

                    else -> Unit
                }
            }
        }
        downloadStatusEvent += listener
        while (!isDone) {
            println("AWAITING $id")
            delay(1000)
        }
        downloadStatusEvent -= listener
    }

    private fun handleNotification(id: Int, notification: Notification) {
        main {
            if (SDK_INT >= 29)
            setForegroundAsync(ForegroundInfo(id, notification, FOREGROUND_SERVICE_TYPE_DATA_SYNC))
            else setForegroundAsync(ForegroundInfo(id, notification))

        }
    }
}