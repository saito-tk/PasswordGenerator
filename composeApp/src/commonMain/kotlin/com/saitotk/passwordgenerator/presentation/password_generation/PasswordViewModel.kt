package com.saitotk.passwordgenerator.presentation.password_generation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saitotk.passwordgenerator.core.clipboard.ClipboardManager
import com.saitotk.passwordgenerator.domain.model.PasswordConfig
import com.saitotk.passwordgenerator.domain.usecase.GeneratePasswordsUseCase
import com.saitotk.passwordgenerator.domain.usecase.GetPasswordConfigUseCase
import com.saitotk.passwordgenerator.domain.usecase.SavePasswordConfigUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class PasswordViewModel(
    private val generatePasswordsUseCase: GeneratePasswordsUseCase,
    private val savePasswordConfigUseCase: SavePasswordConfigUseCase,
    private val getPasswordConfigUseCase: GetPasswordConfigUseCase,
    private val clipboardManager: ClipboardManager
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(PasswordUiState())
    val uiState: StateFlow<PasswordUiState> = _uiState.asStateFlow()
    
    init {
        loadConfig()
    }
    
    fun onEvent(event: PasswordEvent) {
        when (event) {
            is PasswordEvent.UpdateLength -> updateLength(event.length)
            is PasswordEvent.UpdateCount -> updateCount(event.count)
            is PasswordEvent.UpdateUseUppercase -> updateUseUppercase(event.use)
            is PasswordEvent.UpdateUseLowercase -> updateUseLowercase(event.use)
            is PasswordEvent.UpdateUseNumbers -> updateUseNumbers(event.use)
            is PasswordEvent.UpdateUseSymbols -> updateUseSymbols(event.use)
            is PasswordEvent.UpdateSelectedSymbol -> updateSelectedSymbol(event.symbol, event.selected)
            is PasswordEvent.UpdateCustomSymbols -> updateCustomSymbols(event.symbols)
            is PasswordEvent.UpdateAvoidRepeatingChars -> updateAvoidRepeatingChars(event.avoid)
            is PasswordEvent.SelectAllSymbols -> selectAllSymbols(event.selectAll)
            is PasswordEvent.GeneratePasswords -> generatePasswords()
            is PasswordEvent.HidePasswordResults -> hidePasswordResults()
            is PasswordEvent.ShowPasswordResults -> showPasswordResults()
            is PasswordEvent.ClearError -> clearError()
            is PasswordEvent.CopyPassword -> copyPassword(event.password)
        }
    }
    
    private fun loadConfig() {
        getPasswordConfigUseCase()
            .catch { 
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "設定の読み込みに失敗しました"
                )
            }
            .onEach { config ->
                _uiState.value = _uiState.value.copy(
                    config = config,
                    isLoading = false,
                    isSelectAllSymbols = config.selectedSymbols.containsAll(config.availableSymbols)
                )
            }
            .launchIn(viewModelScope)
    }
    
    private fun updateLength(length: Int) {
        val validLength = length.coerceIn(4, 100_000_000) // 1億桁まで
        updateConfig { it.copy(length = validLength) }
    }
    
    private fun updateCount(count: Int) {
        val validCount = count.coerceIn(1, 25)
        updateConfig { it.copy(count = validCount) }
    }
    
    private fun updateUseUppercase(use: Boolean) {
        updateConfig { it.copy(useUppercase = use) }
    }
    
    private fun updateUseLowercase(use: Boolean) {
        updateConfig { it.copy(useLowercase = use) }
    }
    
    private fun updateUseNumbers(use: Boolean) {
        updateConfig { it.copy(useNumbers = use) }
    }
    
    private fun updateUseSymbols(use: Boolean) {
        updateConfig { it.copy(useSymbols = use) }
    }
    
    private fun updateSelectedSymbol(symbol: String, selected: Boolean) {
        val currentSymbols = _uiState.value.config.selectedSymbols.toMutableSet()
        if (selected) {
            currentSymbols.add(symbol)
        } else {
            currentSymbols.remove(symbol)
        }
        
        updateConfig { it.copy(selectedSymbols = currentSymbols) }
        
        // Update select all state
        _uiState.value = _uiState.value.copy(
            isSelectAllSymbols = currentSymbols.containsAll(_uiState.value.config.availableSymbols)
        )
    }
    
    private fun updateCustomSymbols(symbols: String) {
        updateConfig { it.copy(customSymbols = symbols) }
    }
    
    private fun updateAvoidRepeatingChars(avoid: Boolean) {
        updateConfig { it.copy(avoidRepeatingChars = avoid) }
    }
    
    private fun selectAllSymbols(selectAll: Boolean) {
        val newSymbols = if (selectAll) {
            _uiState.value.config.availableSymbols
        } else {
            emptySet()
        }
        
        updateConfig { it.copy(selectedSymbols = newSymbols) }
        _uiState.value = _uiState.value.copy(isSelectAllSymbols = selectAll)
    }
    
    private fun generatePasswords() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isGenerating = true, errorMessage = null)
            
            val result = generatePasswordsUseCase(_uiState.value.config)
            
            result.fold(
                onSuccess = { passwords ->
                    _uiState.value = _uiState.value.copy(
                        generatedPasswords = passwords,
                        showPasswordResults = true,
                        isGenerating = false
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        errorMessage = error.message ?: "パスワード生成に失敗しました",
                        isGenerating = false
                    )
                }
            )
        }
    }
    
    private fun hidePasswordResults() {
        _uiState.value = _uiState.value.copy(showPasswordResults = false)
    }
    
    private fun showPasswordResults() {
        _uiState.value = _uiState.value.copy(showPasswordResults = true)
    }
    
    private fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
    
    private fun copyPassword(password: String) {
        clipboardManager.copyToClipboard(password)
    }
    
    private fun updateConfig(update: (PasswordConfig) -> PasswordConfig) {
        val newConfig = update(_uiState.value.config)
        _uiState.value = _uiState.value.copy(config = newConfig)
        
        // 設定を保存
        viewModelScope.launch {
            savePasswordConfigUseCase(newConfig)
        }
    }
}