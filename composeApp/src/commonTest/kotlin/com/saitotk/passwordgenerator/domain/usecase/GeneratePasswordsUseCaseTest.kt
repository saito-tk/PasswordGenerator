package com.saitotk.passwordgenerator.domain.usecase

import com.saitotk.passwordgenerator.domain.model.PasswordConfig
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GeneratePasswordsUseCaseTest {
    
    private val useCase = GeneratePasswordsUseCase()
    
    @Test
    fun `generate passwords with valid config returns success`() {
        val config = PasswordConfig(
            length = 8,
            count = 3,
            useUppercase = true,
            useLowercase = true,
            useNumbers = true,
            useSymbols = false
        )
        
        val result = useCase(config)
        
        assertTrue(result.isSuccess)
        val passwords = result.getOrNull()!!
        assertEquals(3, passwords.size)
        passwords.forEach { password ->
            assertEquals(8, password.value.length)
            // 生成されたパスワードが期待する文字種を含んでいるかチェックするのではなく、
            // 正しいパスワード生成用の文字セットから生成されているかをチェックする
            val allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
            assertTrue(password.value.all { it in allowedChars })
        }
    }
    
    @Test
    fun `generate passwords with symbols includes selected symbols`() {
        val config = PasswordConfig(
            length = 12,
            count = 1,
            useUppercase = false,
            useLowercase = false,
            useNumbers = false,
            useSymbols = true,
            selectedSymbols = setOf("@", "#", "$")
        )
        
        val result = useCase(config)
        
        assertTrue(result.isSuccess)
        val password = result.getOrNull()!!.first()
        assertEquals(12, password.value.length)
        assertTrue(password.value.all { it in "@#$" })
    }
    
    @Test
    fun `generate passwords with custom symbols includes custom symbols`() {
        val config = PasswordConfig(
            length = 8,
            count = 1,
            useUppercase = false,
            useLowercase = false,
            useNumbers = false,
            useSymbols = true,
            customSymbols = "xyz"
        )
        
        val result = useCase(config)
        
        assertTrue(result.isSuccess)
        val password = result.getOrNull()!!.first()
        assertEquals(8, password.value.length)
        assertTrue(password.value.all { it in "xyz" })
    }
    
    @Test
    fun `generate passwords with avoid repeating chars does not have consecutive chars`() {
        val config = PasswordConfig(
            length = 10,
            count = 5,
            useUppercase = false,
            useLowercase = false,
            useNumbers = false,
            useSymbols = true,
            selectedSymbols = setOf("A", "B", "C", "D", "E"),
            avoidRepeatingChars = true
        )
        
        val result = useCase(config)
        
        assertTrue(result.isSuccess)
        val passwords = result.getOrNull()!!
        passwords.forEach { password ->
            val chars = password.value.toCharArray()
            for (i in 0 until chars.size - 1) {
                assertFalse(chars[i] == chars[i + 1], "Found consecutive chars in: ${password.value}")
            }
        }
    }
    
    @Test
    fun `generate passwords with invalid config returns failure`() {
        val config = PasswordConfig(
            length = 2, // Too short
            count = 1,
            useUppercase = false,
            useLowercase = false,
            useNumbers = false,
            useSymbols = false
        )
        
        val result = useCase(config)
        
        assertTrue(result.isFailure)
    }
    
    @Test
    fun `generate passwords with no character types returns failure`() {
        val config = PasswordConfig(
            length = 8,
            count = 1,
            useUppercase = false,
            useLowercase = false,
            useNumbers = false,
            useSymbols = false
        )
        
        val result = useCase(config)
        
        assertTrue(result.isFailure)
    }
}