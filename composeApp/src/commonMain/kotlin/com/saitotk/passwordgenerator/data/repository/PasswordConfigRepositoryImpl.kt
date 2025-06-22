package com.saitotk.passwordgenerator.data.repository

import com.saitotk.passwordgenerator.data.datasource.PasswordConfigDataSource
import com.saitotk.passwordgenerator.domain.model.PasswordConfig
import com.saitotk.passwordgenerator.domain.repository.PasswordConfigRepository
import kotlinx.coroutines.flow.Flow

class PasswordConfigRepositoryImpl(
    private val localDataSource: PasswordConfigDataSource
) : PasswordConfigRepository {
    
    override suspend fun saveConfig(config: PasswordConfig) {
        localDataSource.saveConfig(config)
    }
    
    override fun getConfig(): Flow<PasswordConfig> {
        return localDataSource.getConfig()
    }
}