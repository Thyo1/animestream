package com.thyo.animestream.utils

actual fun runOnMainThreadNative(work: () -> Unit) {
    work.invoke()
}