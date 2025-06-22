package com.saitotk.passwordgenerator.di

import android.content.Context
import com.saitotk.passwordgenerator.core.clipboard.ClipboardManager
import org.koin.dsl.module

actual val platformModule = module {
    factory { ClipboardManager(get<Context>()) }
}