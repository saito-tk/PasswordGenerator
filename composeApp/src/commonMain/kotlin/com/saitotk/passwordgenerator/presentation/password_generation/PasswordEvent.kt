package com.saitotk.passwordgenerator.presentation.password_generation

sealed class PasswordEvent {
    data class UpdateLength(val length: Int) : PasswordEvent()
    data class UpdateCount(val count: Int) : PasswordEvent()
    data class UpdateUseUppercase(val use: Boolean) : PasswordEvent()
    data class UpdateUseLowercase(val use: Boolean) : PasswordEvent()
    data class UpdateUseNumbers(val use: Boolean) : PasswordEvent()
    data class UpdateUseSymbols(val use: Boolean) : PasswordEvent()
    data class UpdateSelectedSymbol(val symbol: String, val selected: Boolean) : PasswordEvent()
    data class UpdateCustomSymbols(val symbols: String) : PasswordEvent()
    data class UpdateAvoidRepeatingChars(val avoid: Boolean) : PasswordEvent()
    data class SelectAllSymbols(val selectAll: Boolean) : PasswordEvent()
    data object GeneratePasswords : PasswordEvent()
    data object HidePasswordResults : PasswordEvent()
    data object ShowPasswordResults : PasswordEvent()
    data object ClearError : PasswordEvent()
    data class CopyPassword(val password: String) : PasswordEvent()
}