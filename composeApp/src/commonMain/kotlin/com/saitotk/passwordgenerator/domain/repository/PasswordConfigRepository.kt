package com.saitotk.passwordgenerator.domain.repository

import com.saitotk.passwordgenerator.domain.model.PasswordConfig
import kotlinx.coroutines.flow.Flow

interface PasswordConfigRepository {
    suspend fun saveConfig(config: PasswordConfig)
    fun getConfig(): Flow<PasswordConfig>
}