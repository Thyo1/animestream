package com.thyo.animestream

import android.view.LayoutInflater
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.thyo.animestream.ui.HeaderViewDecoration

fun setHeaderDecoration(view: RecyclerView, @LayoutRes headerViewRes: Int) {
    val headerView = LayoutInflater.from(view.context).inflate(headerViewRes, null)
    view.addItemDecoration(HeaderViewDecoration(headerView))
}