package com.thyo.animestream.utils

import android.os.Handler
import android.os.Looper

actual fun runOnMainThreadNative(work: () -> Unit) {
    val mainHandler = Handler(Looper.getMainLooper())
    mainHandler.post {
        work()
    }
}