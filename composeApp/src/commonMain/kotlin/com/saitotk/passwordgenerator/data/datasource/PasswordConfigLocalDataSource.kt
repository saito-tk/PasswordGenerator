package com.saitotk.passwordgenerator.data.datasource

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.saitotk.passwordgenerator.domain.model.PasswordConfig
import com.saitotk.passwordgenerator.domain.model.RandomAlgorithm
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class PasswordConfigLocalDataSource(
    private val dataStore: DataStore<Preferences>
) : PasswordConfigDataSource {
    
    companion object {
        private val LENGTH_KEY = intPreferencesKey("password_length")
        private val COUNT_KEY = intPreferencesKey("password_count")
        private val USE_UPPERCASE_KEY = booleanPreferencesKey("use_uppercase")
        private val USE_LOWERCASE_KEY = booleanPreferencesKey("use_lowercase")
        private val USE_NUMBERS_KEY = booleanPreferencesKey("use_numbers")
        private val USE_SYMBOLS_KEY = booleanPreferencesKey("use_symbols")
        private val SELECTED_SYMBOLS_KEY = stringPreferencesKey("selected_symbols")
        private val CUSTOM_SYMBOLS_KEY = stringPreferencesKey("custom_symbols")
        private val AVOID_REPEATING_CHARS_KEY = booleanPreferencesKey("avoid_repeating_chars")
        private val RANDOM_ALGORITHM_KEY = stringPreferencesKey("random_algorithm")
    }
    
    override suspend fun saveConfig(config: PasswordConfig) {
        dataStore.edit { preferences ->
            preferences[LENGTH_KEY] = config.length
            preferences[COUNT_KEY] = config.count
            preferences[USE_UPPERCASE_KEY] = config.useUppercase
            preferences[USE_LOWERCASE_KEY] = config.useLowercase
            preferences[USE_NUMBERS_KEY] = config.useNumbers
            preferences[USE_SYMBOLS_KEY] = config.useSymbols
            preferences[SELECTED_SYMBOLS_KEY] = Json.encodeToString(config.selectedSymbols.toList())
            preferences[CUSTOM_SYMBOLS_KEY] = config.customSymbols
            preferences[AVOID_REPEATING_CHARS_KEY] = config.avoidRepeatingChars
            preferences[RANDOM_ALGORITHM_KEY] = config.randomAlgorithm.name
        }
    }
    
    override fun getConfig(): Flow<PasswordConfig> {
        return dataStore.data.map { preferences ->
            val selectedSymbolsList = try {
                Json.decodeFromString<List<String>>(
                    preferences[SELECTED_SYMBOLS_KEY] ?: "[]"
                )
            } catch (e: Exception) {
                emptyList()
            }
            
            val randomAlgorithm = try {
                RandomAlgorithm.valueOf(preferences[RANDOM_ALGORITHM_KEY] ?: RandomAlgorithm.PSEUDO_RANDOM.name)
            } catch (e: Exception) {
                RandomAlgorithm.PSEUDO_RANDOM
            }
            
            PasswordConfig(
                length = preferences[LENGTH_KEY] ?: 12,
                count = preferences[COUNT_KEY] ?: 5,
                useUppercase = preferences[USE_UPPERCASE_KEY] ?: true,
                useLowercase = preferences[USE_LOWERCASE_KEY] ?: true,
                useNumbers = preferences[USE_NUMBERS_KEY] ?: true,
                useSymbols = preferences[USE_SYMBOLS_KEY] ?: false,
                selectedSymbols = selectedSymbolsList.toSet(),
                customSymbols = preferences[CUSTOM_SYMBOLS_KEY] ?: "",
                avoidRepeatingChars = preferences[AVOID_REPEATING_CHARS_KEY] ?: false,
                randomAlgorithm = randomAlgorithm
            )
        }
    }
}