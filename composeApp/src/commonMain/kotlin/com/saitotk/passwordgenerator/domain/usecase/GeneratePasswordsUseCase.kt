package com.saitotk.passwordgenerator.domain.usecase

import com.saitotk.passwordgenerator.core.random.RandomGenerator
import com.saitotk.passwordgenerator.core.random.RandomGeneratorFactory
import com.saitotk.passwordgenerator.domain.model.GeneratedPassword
import com.saitotk.passwordgenerator.domain.model.PasswordConfig

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
            
            if (config.avoidRepeatingChars && charset.length < 2) {
                return Result.failure(IllegalArgumentException("連続文字回避には最低2種類の文字が必要です"))
            }
            
            val passwords = mutableListOf<GeneratedPassword>()
            
            val randomGenerator = RandomGeneratorFactory.create(config.randomAlgorithm)
            
            repeat(config.count) {
                val password = generateSinglePassword(config, charset, randomGenerator)
                passwords.add(GeneratedPassword(password, hasFallback = randomGenerator.hasFallbackOccurred))
            }
            
            Result.success(passwords)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun generateSinglePassword(config: PasswordConfig, charset: String, randomGenerator: RandomGenerator): String {
        if (config.avoidRepeatingChars && charset.length < 2) {
            throw IllegalArgumentException("連続文字回避には最低2種類の文字が必要です")
        }
        
        val password = StringBuilder()
        var lastChar: Char? = null
        var attempts = 0
        
        repeat(config.length) {
            var nextChar: Char
            attempts = 0
            do {
                nextChar = charset[randomGenerator.nextInt(charset.length)]
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