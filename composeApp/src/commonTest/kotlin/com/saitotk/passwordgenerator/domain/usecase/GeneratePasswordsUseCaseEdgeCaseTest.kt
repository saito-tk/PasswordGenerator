package com.saitotk.passwordgenerator.domain.usecase

import com.saitotk.passwordgenerator.domain.model.PasswordConfig
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GeneratePasswordsUseCaseEdgeCaseTest {
    
    private val useCase = GeneratePasswordsUseCase()
    
    @Test
    fun `最小文字セットでパスワードを生成する`() {
        val config = PasswordConfig(
            length = 10,
            count = 1,
            useUppercase = false,
            useLowercase = false,
            useNumbers = false,
            useSymbols = true,
            selectedSymbols = setOf("A"),
            avoidRepeatingChars = false
        )
        
        val result = useCase(config)
        
        assertTrue(result.isSuccess)
        val password = result.getOrNull()!!.first()
        assertEquals(10, password.value.length)
        assertTrue(password.value.all { it == 'A' })
    }
    
    @Test
    fun `連続文字を避ける設定で文字セットが不十分な場合失敗する`() {
        val config = PasswordConfig(
            length = 10,
            count = 1,
            useUppercase = false,
            useLowercase = false,
            useNumbers = false,
            useSymbols = true,
            selectedSymbols = setOf("A", "B"),
            avoidRepeatingChars = true
        )
        
        val result = useCase(config)
        
        assertTrue(result.isFailure)
    }
    
    @Test
    fun `連続文字を避ける設定で文字セットがちょうど良いサイズの場合`() {
        val config = PasswordConfig(
            length = 5,
            count = 1,
            useUppercase = false,
            useLowercase = false,
            useNumbers = false,
            useSymbols = true,
            selectedSymbols = setOf("A", "B", "C", "D", "E"),
            avoidRepeatingChars = true
        )
        
        val result = useCase(config)
        
        assertTrue(result.isSuccess)
        val password = result.getOrNull()!!.first()
        assertEquals(5, password.value.length)
        val chars = password.value.toCharArray()
        for (i in 0 until chars.size - 1) {
            assertFalse(chars[i] == chars[i + 1], "Found consecutive chars in: ${password.value}")
        }
        assertTrue(password.value.all { it in "ABCDE" })
    }
    
    @Test
    fun `連続文字を避ける設定で必要数よりひとつ多い文字セットの場合`() {
        val config = PasswordConfig(
            length = 5,
            count = 1,
            useUppercase = false,
            useLowercase = false,
            useNumbers = false,
            useSymbols = true,
            selectedSymbols = setOf("A", "B", "C", "D", "E", "F"),
            avoidRepeatingChars = true
        )
        
        val result = useCase(config)
        
        assertTrue(result.isSuccess)
        val password = result.getOrNull()!!.first()
        assertEquals(5, password.value.length)
        val chars = password.value.toCharArray()
        for (i in 0 until chars.size - 1) {
            assertFalse(chars[i] == chars[i + 1], "Found consecutive chars in: ${password.value}")
        }
    }
    
    @Test
    fun `特殊文字と空白文字でパスワードを生成する`() {
        val config = PasswordConfig(
            length = 8,
            count = 1,
            useUppercase = false,
            useLowercase = false,
            useNumbers = false,
            useSymbols = true,
            customSymbols = " \t\n\r"
        )
        
        val result = useCase(config)
        
        assertTrue(result.isSuccess)
        val password = result.getOrNull()!!.first()
        assertEquals(8, password.value.length)
        assertTrue(password.value.all { it in " \t\n\r" })
    }
    
    @Test
    fun `非常に長いカスタム記号文字列でパスワードを生成する`() {
        val customSymbols = "A".repeat(1000) + "B".repeat(1000)
        val config = PasswordConfig(
            length = 10,
            count = 1,
            useUppercase = false,
            useLowercase = false,
            useNumbers = false,
            useSymbols = true,
            customSymbols = customSymbols
        )
        
        val result = useCase(config)
        
        assertTrue(result.isSuccess)
        val password = result.getOrNull()!!.first()
        assertEquals(10, password.value.length)
        assertTrue(password.value.all { it in "AB" })
    }
    
    @Test
    fun `フィルタリング後に文字セットが空になる場合失敗する`() {
        val config = PasswordConfig(
            length = 8,
            count = 1,
            useUppercase = false,
            useLowercase = false,
            useNumbers = false,
            useSymbols = true,
            selectedSymbols = emptySet(),
            customSymbols = ""
        )
        
        val result = useCase(config)
        
        assertTrue(result.isFailure)
    }
    
    @Test
    fun `単一文字で連続文字を避ける設定のパスワード生成`() {
        val config = PasswordConfig(
            length = 4,
            count = 1,
            useUppercase = false,
            useLowercase = false,
            useNumbers = false,
            useSymbols = true,
            selectedSymbols = setOf("A"),
            avoidRepeatingChars = true
        )
        
        val result = useCase(config)
        
        assertTrue(result.isFailure)
    }
    
    @Test
    fun `非ASCII文字でパスワードを生成する`() {
        val config = PasswordConfig(
            length = 8,
            count = 1,
            useUppercase = false,
            useLowercase = false,
            useNumbers = false,
            useSymbols = true,
            customSymbols = "αβγδεζηθ"
        )
        
        val result = useCase(config)
        
        assertTrue(result.isSuccess)
        val password = result.getOrNull()!!.first()
        assertEquals(8, password.value.length)
        assertTrue(password.value.all { it in "αβγδεζηθ" })
    }
    
    @Test
    fun `ASCIIと非ASCII文字を混合したパスワード生成`() {
        val config = PasswordConfig(
            length = 12,
            count = 1,
            useUppercase = true,
            useLowercase = true,
            useNumbers = true,
            useSymbols = true,
            customSymbols = "αβγ"
        )
        
        val result = useCase(config)
        
        assertTrue(result.isSuccess)
        val password = result.getOrNull()!!.first()
        assertEquals(12, password.value.length)
        val allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789αβγ"
        assertTrue(password.value.all { it in allowedChars })
    }
    
    @Test
    fun `高い重複可能性で複数パスワードを生成する`() {
        val config = PasswordConfig(
            length = 4,
            count = 10,
            useUppercase = false,
            useLowercase = false,
            useNumbers = false,
            useSymbols = true,
            selectedSymbols = setOf("A", "B"),
            avoidRepeatingChars = false
        )
        
        val result = useCase(config)
        
        assertTrue(result.isSuccess)
        val passwords = result.getOrNull()!!
        assertEquals(10, passwords.size)
        passwords.forEach { password ->
            assertEquals(4, password.value.length)
            assertTrue(password.value.all { it in "AB" })
        }
    }
    
    @Test
    fun `境界長さ値でパスワードを生成する`() {
        val configs = listOf(
            PasswordConfig(length = 4, count = 1, useUppercase = true),
            PasswordConfig(length = 50, count = 1, useUppercase = true),
            PasswordConfig(length = 1000, count = 1, useUppercase = true)
        )
        
        configs.forEach { config ->
            val result = useCase(config)
            assertTrue(result.isSuccess, "Failed for length ${config.length}")
            val password = result.getOrNull()!!.first()
            assertEquals(config.length, password.value.length)
        }
    }
    
    @Test
    fun `境界件数値でパスワードを生成する`() {
        val configs = listOf(
            PasswordConfig(length = 8, count = 1, useUppercase = true),
            PasswordConfig(length = 8, count = 10, useUppercase = true),
            PasswordConfig(length = 8, count = 25, useUppercase = true)
        )
        
        configs.forEach { config ->
            val result = useCase(config)
            assertTrue(result.isSuccess, "Failed for count ${config.count}")
            val passwords = result.getOrNull()!!
            assertEquals(config.count, passwords.size)
        }
    }
    
    @Test
    fun `ストレステスト_大きな文字セットでパスワード生成`() {
        val allSymbols = setOf(
            "!", "@", "#", "$", "%", "^", "&", "*", "(", ")", "-", "_", "=", "+",
            "[", "]", "{", "}", "|", "\\", ":", ";", "\"", "'", "<", ">", ",", ".",
            "?", "/", "~", "`"
        )
        
        val config = PasswordConfig(
            length = 50,
            count = 10,
            useUppercase = true,
            useLowercase = true,
            useNumbers = true,
            useSymbols = true,
            selectedSymbols = allSymbols,
            avoidRepeatingChars = true
        )
        
        val result = useCase(config)
        
        assertTrue(result.isSuccess)
        val passwords = result.getOrNull()!!
        assertEquals(10, passwords.size)
        passwords.forEach { password ->
            assertEquals(50, password.value.length)
            val chars = password.value.toCharArray()
            for (i in 0 until chars.size - 1) {
                assertFalse(chars[i] == chars[i + 1], "Found consecutive chars in: ${password.value}")
            }
        }
    }
    
    @Test
    fun `件数0でパスワード生成が失敗する`() {
        val config = PasswordConfig(
            length = 8,
            count = 0,
            useUppercase = true
        )
        
        val result = useCase(config)
        
        assertTrue(result.isFailure)
    }
    
    @Test
    fun `マイナス件数でパスワード生成が失敗する`() {
        val config = PasswordConfig(
            length = 8,
            count = -1,
            useUppercase = true
        )
        
        val result = useCase(config)
        
        assertTrue(result.isFailure)
    }
    
    @Test
    fun `長さ0でパスワード生成が失敗する`() {
        val config = PasswordConfig(
            length = 0,
            count = 1,
            useUppercase = true
        )
        
        val result = useCase(config)
        
        assertTrue(result.isFailure)
    }
    
    @Test
    fun `マイナス長さでパスワード生成が失敗する`() {
        val config = PasswordConfig(
            length = -1,
            count = 1,
            useUppercase = true
        )
        
        val result = useCase(config)
        
        assertTrue(result.isFailure)
    }
}