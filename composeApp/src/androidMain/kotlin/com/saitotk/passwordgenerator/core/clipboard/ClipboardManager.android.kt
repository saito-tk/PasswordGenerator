package com.saitotk.passwordgenerator.core.clipboard

import android.content.ClipData
import android.content.ClipboardManager as AndroidClipboardManager
import android.content.Context

actual class ClipboardManager(private val context: Context) {
    actual fun copyToClipboard(text: String) {
        val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as AndroidClipboardManager
        val clip = ClipData.newPlainText("Password", text)
        clipboardManager.setPrimaryClip(clip)
    }
}