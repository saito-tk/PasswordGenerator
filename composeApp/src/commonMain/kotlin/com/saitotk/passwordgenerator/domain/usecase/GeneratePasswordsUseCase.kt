package com.saitotk.passwordgenerator.domain.usecase

import com.saitotk.passwordgenerator.domain.model.GeneratedPassword
import com.saitotk.passwordgenerator.domain.model.PasswordConfig
import kotlin.random.Random

class GeneratePasswordsUseCase {
    
    operator fun invoke(config: PasswordConfig): Result<List<GeneratedPassword>> {
        return try {
            if (!config.isValid()) {
                return Result.failure(IllegalArgumentException("Invalid password configuration"))
            }
            
            val charset = config.getCharacterSet()
            if (charset.isEmpty()) {
                return Result.failure(IllegalArgumentException("No character types selected"))
            }
            
            if (config.avoidRepeatingChars && charset.length < config.length) {
                return Result.failure(IllegalArgumentException("Not enough unique characters for avoid repeating chars"))
            }
            
            val passwords = mutableListOf<GeneratedPassword>()
            
            repeat(config.count) {
                val password = generateSinglePassword(config, charset)
                passwords.add(GeneratedPassword(password))
            }
            
            Result.success(passwords)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun generateSinglePassword(config: PasswordConfig, charset: String): String {
        if (config.avoidRepeatingChars && charset.length < config.length) {
            throw IllegalArgumentException("Not enough unique characters for avoid repeating chars")
        }
        
        val password = StringBuilder()
        var lastChar: Char? = null
        var attempts = 0
        
        repeat(config.length) {
            var nextChar: Char
            attempts = 0
            do {
                nextChar = charset[Random.nextInt(charset.length)]
                attempts++
                if (attempts > 1000) {
                    throw IllegalStateException("Could not generate password without repeating characters")
                }
            } while (config.avoidRepeatingChars && nextChar == lastChar && charset.length > 1)
            
            password.append(nextChar)
            lastChar = nextChar
        }
        
        return password.toString()
    }
}