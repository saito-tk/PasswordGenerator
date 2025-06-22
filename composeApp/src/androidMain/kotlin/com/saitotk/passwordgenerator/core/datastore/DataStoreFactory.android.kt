package com.saitotk.passwordgenerator.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import okio.Path.Companion.toPath

actual fun createDataStore(): DataStore<Preferences> {
    return PreferenceDataStoreFactory.createWithPath(
        produceFile = { "/data/data/com.saitotk.passwordgenerator/files/password_config.preferences_pb".toPath() }
    )
}