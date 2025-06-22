package com.saitotk.passwordgenerator.domain.usecase

import com.saitotk.passwordgenerator.domain.model.PasswordConfig
import com.saitotk.passwordgenerator.domain.repository.PasswordConfigRepository
import kotlinx.coroutines.flow.Flow

class GetPasswordConfigUseCase(
    private val repository: PasswordConfigRepository
) {
    operator fun invoke(): Flow<PasswordConfig> {
        return repository.getConfig()
    }
}