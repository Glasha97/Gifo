package com.gifo.common.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE

fun Context.copyToClipboard(text: String) {
    val clipboard: ClipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("GIF", text)
    clipboard.setPrimaryClip(clip)
}