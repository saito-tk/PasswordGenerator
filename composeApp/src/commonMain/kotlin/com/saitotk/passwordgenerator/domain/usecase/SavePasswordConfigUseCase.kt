package com.saitotk.passwordgenerator.domain.usecase

import com.saitotk.passwordgenerator.domain.model.PasswordConfig
import com.saitotk.passwordgenerator.domain.repository.PasswordConfigRepository

class SavePasswordConfigUseCase(
    private val repository: PasswordConfigRepository
) {
    suspend operator fun invoke(config: PasswordConfig): Result<Unit> {
        return try {
            if (!config.isValid()) {
                return Result.failure(IllegalArgumentException("Invalid password configuration"))
            }
            repository.saveConfig(config)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}