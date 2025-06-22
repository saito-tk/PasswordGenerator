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
        val password = StringBuilder()
        var lastChar: Char? = null
        
        repeat(config.length) {
            var nextChar: Char
            do {
                nextChar = charset[Random.nextInt(charset.length)]
            } while (config.avoidRepeatingChars && nextChar == lastChar && charset.length > 1)
            
            password.append(nextChar)
            lastChar = nextChar
        }
        
        return password.toString()
    }
}