package com.saitotk.passwordgenerator.di

import com.saitotk.passwordgenerator.core.clipboard.ClipboardManager
import com.saitotk.passwordgenerator.core.datastore.createDataStore
import com.saitotk.passwordgenerator.data.datasource.PasswordConfigDataSource
import com.saitotk.passwordgenerator.data.datasource.PasswordConfigLocalDataSource
import com.saitotk.passwordgenerator.data.repository.PasswordConfigRepositoryImpl
import com.saitotk.passwordgenerator.domain.repository.PasswordConfigRepository
import com.saitotk.passwordgenerator.domain.usecase.GeneratePasswordsUseCase
import com.saitotk.passwordgenerator.domain.usecase.GetPasswordConfigUseCase
import com.saitotk.passwordgenerator.domain.usecase.SavePasswordConfigUseCase
import com.saitotk.passwordgenerator.presentation.password_generation.PasswordViewModel
import org.koin.dsl.module

val appModule = module {
    // DataStore
    single { createDataStore() }
    
    // Data Sources
    single<PasswordConfigDataSource> { 
        PasswordConfigLocalDataSource(get()) 
    }
    
    // Repositories
    single<PasswordConfigRepository> { 
        PasswordConfigRepositoryImpl(get()) 
    }
    
    // Use Cases
    single { GeneratePasswordsUseCase() }
    single { SavePasswordConfigUseCase(get()) }
    single { GetPasswordConfigUseCase(get()) }
    
    // ViewModels
    factory { 
        PasswordViewModel(
            generatePasswordsUseCase = get(),
            savePasswordConfigUseCase = get(),
            getPasswordConfigUseCase = get(),
            clipboardManager = get()
        ) 
    }
}