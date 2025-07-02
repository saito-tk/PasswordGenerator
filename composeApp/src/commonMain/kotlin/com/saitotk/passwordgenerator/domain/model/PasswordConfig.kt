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
    val avoidRepeatingChars: Boolean = false,
    val randomAlgorithm: RandomAlgorithm = RandomAlgorithm.PSEUDO_RANDOM
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
        val charSet = mutableSetOf<Char>()
        
        if (useUppercase) {
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ".forEach { charSet.add(it) }
        }
        if (useLowercase) {
            "abcdefghijklmnopqrstuvwxyz".forEach { charSet.add(it) }
        }
        if (useNumbers) {
            "0123456789".forEach { charSet.add(it) }
        }
        if (useSymbols) {
            getAllSymbols().forEach { charSet.add(it) }
        }
        
        return charSet.joinToString("")
    }
    
    fun isValid(): Boolean {
        if (length < 4 || length > 9_999_999 || count !in 1..25) {
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