package com.saitotk.passwordgenerator

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.saitotk.passwordgenerator.di.appModule
import com.saitotk.passwordgenerator.di.platformModule
import com.saitotk.passwordgenerator.presentation.password_generation.PasswordEvent
import com.saitotk.passwordgenerator.presentation.password_generation.PasswordResultBottomSheet
import com.saitotk.passwordgenerator.presentation.password_generation.PasswordScreen
import com.saitotk.passwordgenerator.presentation.password_generation.PasswordViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject

@Composable
@Preview
fun App() {
    KoinApplication(application = {
        modules(appModule, platformModule)
    }) {
        MaterialTheme {
            PasswordGeneratorApp()
        }
    }
}

@Composable
private fun PasswordGeneratorApp() {
    val viewModel: PasswordViewModel = koinInject()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    Scaffold { paddingValues ->
        PasswordScreen(
            uiState = uiState,
            onEvent = viewModel::onEvent
        )
        
        if (uiState.showPasswordResults && uiState.generatedPasswords.isNotEmpty()) {
            PasswordResultBottomSheet(
                passwords = uiState.generatedPasswords,
                onDismiss = { viewModel.onEvent(PasswordEvent.HidePasswordResults) },
                onPasswordClick = { password ->
                    viewModel.onEvent(PasswordEvent.CopyPassword(password))
                }
            )
        }
    }
}