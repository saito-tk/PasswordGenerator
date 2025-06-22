package com.saitotk.passwordgenerator.presentation.password_generation

import com.saitotk.passwordgenerator.domain.model.GeneratedPassword
import com.saitotk.passwordgenerator.domain.model.PasswordConfig

data class PasswordUiState(
    val config: PasswordConfig = PasswordConfig(),
    val generatedPasswords: List<GeneratedPassword> = emptyList(),
    val isGenerating: Boolean = false,
    val showPasswordResults: Boolean = false,
    val errorMessage: String? = null,
    val isSelectAllSymbols: Boolean = false,
    val isLoading: Boolean = true
) {
    val isGenerateButtonEnabled: Boolean
        get() = !isGenerating && config.isValid()
}