package com.saitotk.passwordgenerator.data.datasource

import com.saitotk.passwordgenerator.domain.model.PasswordConfig
import kotlinx.coroutines.flow.Flow

interface PasswordConfigDataSource {
    suspend fun saveConfig(config: PasswordConfig)
    fun getConfig(): Flow<PasswordConfig>
}