package com.saitotk.passwordgenerator.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class PasswordConfig(
    val length: Int = 12,
    val count: Int = 5,
    val useUppercase: Boolean = true,
    val useLowercase: Boolean = true,
    val useNumbers: Boolean = true,
    val useSymbols: Boolean = false,
    val selectedSymbols: Set<String> = emptySet(),
    val customSymbols: String = "",
    val avoidRepeatingChars: Boolean = false
) {
    val availableSymbols = setOf(
        "-", "_", "@", "/", "*", "+", ",", "!", "?", "#", "$", "%", "&",
        "(", ")", "{", "}", "[", "]", "~", "|", ":", ";", "\"", "'",
        "^", ">", "<", "="
    )
    
    fun getAllSymbols(): String {
        val symbols = selectedSymbols.toMutableSet()
        customSymbols.forEach { char ->
            symbols.add(char.toString())
        }
        return symbols.joinToString("")
    }
    
    fun getCharacterSet(): String {
        val charset = StringBuilder()
        
        if (useUppercase) charset.append("ABCDEFGHIJKLMNOPQRSTUVWXYZ")
        if (useLowercase) charset.append("abcdefghijklmnopqrstuvwxyz")
        if (useNumbers) charset.append("0123456789")
        if (useSymbols) charset.append(getAllSymbols())
        
        return charset.toString()
    }
    
    fun isValid(): Boolean {
        if (length < 4 || length > 100_000_000 || count !in 1..25) {
            return false
        }
        
        if (!useUppercase && !useLowercase && !useNumbers && !useSymbols) {
            return false
        }
        
        if (useSymbols && getAllSymbols().isEmpty()) {
            return false
        }
        
        return true
    }
}