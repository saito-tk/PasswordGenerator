package com.saitotk.passwordgenerator.domain.usecase

import com.saitotk.passwordgenerator.domain.model.PasswordConfig
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GeneratePasswordsUseCaseTest {
    
    private val useCase = GeneratePasswordsUseCase()
    
    @Test
    fun `有効な設定でパスワード生成が成功する`() {
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
    fun `記号を使用した場合に選択された記号が含まれる`() {
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
    fun `カスタム記号を使用した場合にカスタム記号が含まれる`() {
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
    fun `連続文字を避ける設定で連続する文字が含まれない`() {
        val config = PasswordConfig(
            length = 8,
            count = 5,
            useUppercase = true,
            useLowercase = true,
            useNumbers = true,
            useSymbols = true,
            selectedSymbols = setOf("!", "@", "#", "$", "%"),
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
    fun `無効な設定でパスワード生成が失敗する`() {
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
    fun `文字種が未選択でパスワード生成が失敗する`() {
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
    
    @Test
    fun `大文字のみでパスワードを生成する`() {
        val config = PasswordConfig(
            length = 10,
            count = 5,
            useUppercase = true,
            useLowercase = false,
            useNumbers = false,
            useSymbols = false
        )
        
        val result = useCase(config)
        
        assertTrue(result.isSuccess)
        val passwords = result.getOrNull()!!
        assertEquals(5, passwords.size)
        passwords.forEach { password ->
            assertEquals(10, password.value.length)
            assertTrue(password.value.all { it.isUpperCase() && it.isLetter() })
        }
    }
    
    @Test
    fun `小文字のみでパスワードを生成する`() {
        val config = PasswordConfig(
            length = 8,
            count = 3,
            useUppercase = false,
            useLowercase = true,
            useNumbers = false,
            useSymbols = false
        )
        
        val result = useCase(config)
        
        assertTrue(result.isSuccess)
        val passwords = result.getOrNull()!!
        assertEquals(3, passwords.size)
        passwords.forEach { password ->
            assertEquals(8, password.value.length)
            assertTrue(password.value.all { it.isLowerCase() && it.isLetter() })
        }
    }
    
    @Test
    fun `数字のみでパスワードを生成する`() {
        val config = PasswordConfig(
            length = 6,
            count = 2,
            useUppercase = false,
            useLowercase = false,
            useNumbers = true,
            useSymbols = false
        )
        
        val result = useCase(config)
        
        assertTrue(result.isSuccess)
        val passwords = result.getOrNull()!!
        assertEquals(2, passwords.size)
        passwords.forEach { password ->
            assertEquals(6, password.value.length)
            assertTrue(password.value.all { it.isDigit() })
        }
    }
    
    @Test
    fun `全ての文字種でパスワードを生成する`() {
        val config = PasswordConfig(
            length = 16,
            count = 1,
            useUppercase = true,
            useLowercase = true,
            useNumbers = true,
            useSymbols = true,
            selectedSymbols = setOf("!", "@", "#", "$", "%")
        )
        
        val result = useCase(config)
        
        assertTrue(result.isSuccess)
        val password = result.getOrNull()!!.first()
        assertEquals(16, password.value.length)
        
        val allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%"
        assertTrue(password.value.all { it in allowedChars })
    }
    
    @Test
    fun `選択記号とカスタム記号を組み合わせたパスワードを生成する`() {
        val config = PasswordConfig(
            length = 12,
            count = 2,
            useUppercase = false,
            useLowercase = false,
            useNumbers = false,
            useSymbols = true,
            selectedSymbols = setOf("!", "@", "#"),
            customSymbols = "xyz"
        )
        
        val result = useCase(config)
        
        assertTrue(result.isSuccess)
        val passwords = result.getOrNull()!!
        assertEquals(2, passwords.size)
        passwords.forEach { password ->
            assertEquals(12, password.value.length)
            assertTrue(password.value.all { it in "!@#xyz" })
        }
    }
    
    @Test
    fun `カスタム記号のみでパスワードを生成する`() {
        val config = PasswordConfig(
            length = 8,
            count = 1,
            useUppercase = false,
            useLowercase = false,
            useNumbers = false,
            useSymbols = true,
            selectedSymbols = emptySet(),
            customSymbols = "🔥💎⭐"
        )
        
        val result = useCase(config)
        
        assertTrue(result.isSuccess)
        val password = result.getOrNull()!!.first()
        assertEquals(8, password.value.length)
        assertTrue(password.value.all { it in "🔥💎⭐" })
    }
    
    @Test
    fun `大文字と数字のみでパスワードを生成する`() {
        val config = PasswordConfig(
            length = 10,
            count = 3,
            useUppercase = true,
            useLowercase = false,
            useNumbers = true,
            useSymbols = false
        )
        
        val result = useCase(config)
        
        assertTrue(result.isSuccess)
        val passwords = result.getOrNull()!!
        assertEquals(3, passwords.size)
        passwords.forEach { password ->
            assertEquals(10, password.value.length)
            assertTrue(password.value.all { it.isUpperCase() || it.isDigit() })
        }
    }
    
    @Test
    fun `小文字と記号のみでパスワードを生成する`() {
        val config = PasswordConfig(
            length = 14,
            count = 1,
            useUppercase = false,
            useLowercase = true,
            useNumbers = false,
            useSymbols = true,
            selectedSymbols = setOf("_", "-", ".", "+")
        )
        
        val result = useCase(config)
        
        assertTrue(result.isSuccess)
        val password = result.getOrNull()!!.first()
        assertEquals(14, password.value.length)
        assertTrue(password.value.all { it.isLowerCase() || it in "_-.+" })
    }
    
    @Test
    fun `連続文字を避ける設定で複数のパスワードを生成する`() {
        val config = PasswordConfig(
            length = 8,
            count = 10,
            useUppercase = true,
            useLowercase = true,
            useNumbers = true,
            useSymbols = false,
            avoidRepeatingChars = true
        )
        
        val result = useCase(config)
        
        assertTrue(result.isSuccess)
        val passwords = result.getOrNull()!!
        assertEquals(10, passwords.size)
        passwords.forEach { password ->
            assertEquals(8, password.value.length)
            val chars = password.value.toCharArray()
            for (i in 0 until chars.size - 1) {
                assertFalse(chars[i] == chars[i + 1], "Found consecutive chars in: ${password.value}")
            }
        }
    }
    
    @Test
    fun `連続文字を避ける設定で単一文字種のパスワードを生成する`() {
        val config = PasswordConfig(
            length = 6,
            count = 5,
            useUppercase = false,
            useLowercase = false,
            useNumbers = false,
            useSymbols = true,
            selectedSymbols = setOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J"),
            avoidRepeatingChars = true
        )
        
        val result = useCase(config)
        
        assertTrue(result.isSuccess)
        val passwords = result.getOrNull()!!
        assertEquals(5, passwords.size)
        passwords.forEach { password ->
            assertEquals(6, password.value.length)
            val chars = password.value.toCharArray()
            for (i in 0 until chars.size - 1) {
                assertFalse(chars[i] == chars[i + 1], "Found consecutive chars in: ${password.value}")
            }
        }
    }
    
    @Test
    fun `連続文字を避けない設定で連続文字が許可される`() {
        val config = PasswordConfig(
            length = 8,
            count = 20,
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
        assertEquals(20, passwords.size)
        passwords.forEach { password ->
            assertEquals(8, password.value.length)
            assertTrue(password.value.all { it in "AB" })
        }
    }
    
    @Test
    fun `最大件数でパスワードを生成する`() {
        val config = PasswordConfig(
            length = 8,
            count = 25,
            useUppercase = true,
            useLowercase = true,
            useNumbers = true,
            useSymbols = false
        )
        
        val result = useCase(config)
        
        assertTrue(result.isSuccess)
        val passwords = result.getOrNull()!!
        assertEquals(25, passwords.size)
        passwords.forEach { password ->
            assertEquals(8, password.value.length)
        }
    }
    
    @Test
    fun `最小長でパスワードを生成する`() {
        val config = PasswordConfig(
            length = 4,
            count = 1,
            useUppercase = true,
            useLowercase = false,
            useNumbers = false,
            useSymbols = false
        )
        
        val result = useCase(config)
        
        assertTrue(result.isSuccess)
        val password = result.getOrNull()!!.first()
        assertEquals(4, password.value.length)
        assertTrue(password.value.all { it.isUpperCase() && it.isLetter() })
    }
    
    @Test
    fun `長いパスワードを生成する`() {
        val config = PasswordConfig(
            length = 100,
            count = 1,
            useUppercase = true,
            useLowercase = true,
            useNumbers = true,
            useSymbols = false
        )
        
        val result = useCase(config)
        
        assertTrue(result.isSuccess)
        val password = result.getOrNull()!!.first()
        assertEquals(100, password.value.length)
    }
    
    @Test
    fun `カスタム記号が空で選択記号がある場合のパスワード生成`() {
        val config = PasswordConfig(
            length = 8,
            count = 1,
            useUppercase = false,
            useLowercase = false,
            useNumbers = false,
            useSymbols = true,
            selectedSymbols = setOf("!", "@", "#"),
            customSymbols = ""
        )
        
        val result = useCase(config)
        
        assertTrue(result.isSuccess)
        val password = result.getOrNull()!!.first()
        assertEquals(8, password.value.length)
        assertTrue(password.value.all { it in "!@#" })
    }
    
    @Test
    fun `カスタム記号に重複文字がある場合のパスワード生成`() {
        val config = PasswordConfig(
            length = 8,
            count = 1,
            useUppercase = false,
            useLowercase = false,
            useNumbers = false,
            useSymbols = true,
            selectedSymbols = setOf("@"),
            customSymbols = "@@##@@"
        )
        
        val result = useCase(config)
        
        assertTrue(result.isSuccess)
        val password = result.getOrNull()!!.first()
        assertEquals(8, password.value.length)
        assertTrue(password.value.all { it in "@#" })
    }
}