package com.saitotk.passwordgenerator.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PasswordConfigTest {
    
    @Test
    fun `有効な設定でisValidがtrueを返す`() {
        val config = PasswordConfig(
            length = 8,
            count = 5,
            useUppercase = true,
            useLowercase = true,
            useNumbers = true,
            useSymbols = false
        )
        
        assertTrue(config.isValid())
    }
    
    @Test
    fun `長さ4未満でisValidがfalseを返す`() {
        val config = PasswordConfig(
            length = 3,
            count = 5,
            useUppercase = true
        )
        
        assertFalse(config.isValid())
    }
    
    @Test
    fun `長さ999万を超えてisValidがfalseを返す`() {
        val config = PasswordConfig(
            length = 10_000_000,
            count = 5,
            useUppercase = true
        )
        
        assertFalse(config.isValid())
    }
    
    @Test
    fun `長さが最大値でisValidがtrueを返す`() {
        val config = PasswordConfig(
            length = 9_999_999,
            count = 5,
            useUppercase = true
        )
        
        assertTrue(config.isValid())
    }
    
    @Test
    fun `件数1未満でisValidがfalseを返す`() {
        val config = PasswordConfig(
            length = 8,
            count = 0,
            useUppercase = true
        )
        
        assertFalse(config.isValid())
    }
    
    @Test
    fun `件数25を超えてisValidがfalseを返す`() {
        val config = PasswordConfig(
            length = 8,
            count = 26,
            useUppercase = true
        )
        
        assertFalse(config.isValid())
    }
    
    @Test
    fun `文字種が未選択でisValidがfalseを返す`() {
        val config = PasswordConfig(
            length = 8,
            count = 5,
            useUppercase = false,
            useLowercase = false,
            useNumbers = false,
            useSymbols = false
        )
        
        assertFalse(config.isValid())
    }
    
    @Test
    fun `大文字のみで正しい文字セットを返す`() {
        val config = PasswordConfig(
            useUppercase = true,
            useLowercase = false,
            useNumbers = false,
            useSymbols = false
        )
        
        assertEquals("ABCDEFGHIJKLMNOPQRSTUVWXYZ", config.getCharacterSet())
    }
    
    @Test
    fun `小文字のみで正しい文字セットを返す`() {
        val config = PasswordConfig(
            useUppercase = false,
            useLowercase = true,
            useNumbers = false,
            useSymbols = false
        )
        
        assertEquals("abcdefghijklmnopqrstuvwxyz", config.getCharacterSet())
    }
    
    @Test
    fun `数字のみで正しい文字セットを返す`() {
        val config = PasswordConfig(
            useUppercase = false,
            useLowercase = false,
            useNumbers = true,
            useSymbols = false
        )
        
        assertEquals("0123456789", config.getCharacterSet())
    }
    
    @Test
    fun `記号のみで正しい文字セットを返す`() {
        val config = PasswordConfig(
            useUppercase = false,
            useLowercase = false,
            useNumbers = false,
            useSymbols = true,
            selectedSymbols = setOf("@", "#", "$")
        )
        
        val charset = config.getCharacterSet()
        assertTrue(charset.contains("@"))
        assertTrue(charset.contains("#"))
        assertTrue(charset.contains("$"))
        assertEquals(3, charset.length)
    }
    
    @Test
    fun `選択された全ての文字種を組み合わせる`() {
        val config = PasswordConfig(
            useUppercase = true,
            useLowercase = true,
            useNumbers = true,
            useSymbols = true,
            selectedSymbols = setOf("@", "#")
        )
        
        val charset = config.getCharacterSet()
        assertTrue(charset.contains("A"))
        assertTrue(charset.contains("a"))
        assertTrue(charset.contains("1"))
        assertTrue(charset.contains("@"))
        assertTrue(charset.contains("#"))
    }
    
    @Test
    fun `選択記号とカスタム記号の両方を含む`() {
        val config = PasswordConfig(
            selectedSymbols = setOf("@", "#"),
            customSymbols = "xyz"
        )
        
        val allSymbols = config.getAllSymbols()
        assertTrue(allSymbols.contains("@"))
        assertTrue(allSymbols.contains("#"))
        assertTrue(allSymbols.contains("x"))
        assertTrue(allSymbols.contains("y"))
        assertTrue(allSymbols.contains("z"))
    }
    
    @Test
    fun `最小長さ4でisValidがtrueを返す`() {
        val config = PasswordConfig(
            length = 4,
            count = 1,
            useUppercase = true
        )
        
        assertTrue(config.isValid())
    }
    
    @Test
    fun `最大件数25でisValidがtrueを返す`() {
        val config = PasswordConfig(
            length = 8,
            count = 25,
            useUppercase = true
        )
        
        assertTrue(config.isValid())
    }
    
    @Test
    fun `単一文字種でisValidがtrueを返す`() {
        val config = PasswordConfig(
            length = 8,
            count = 5,
            useUppercase = true,
            useLowercase = false,
            useNumbers = false,
            useSymbols = false
        )
        
        assertTrue(config.isValid())
    }
    
    @Test
    fun `記号のみ選択し選択記号がある場合isValidがtrueを返す`() {
        val config = PasswordConfig(
            length = 8,
            count = 5,
            useUppercase = false,
            useLowercase = false,
            useNumbers = false,
            useSymbols = true,
            selectedSymbols = setOf("@", "#", "$")
        )
        
        assertTrue(config.isValid())
    }
    
    @Test
    fun `記号のみ選択しカスタム記号がある場合isValidがtrueを返す`() {
        val config = PasswordConfig(
            length = 8,
            count = 5,
            useUppercase = false,
            useLowercase = false,
            useNumbers = false,
            useSymbols = true,
            customSymbols = "xyz"
        )
        
        assertTrue(config.isValid())
    }
    
    @Test
    fun `記号選択したが実際の記号がない場合isValidがfalseを返す`() {
        val config = PasswordConfig(
            length = 8,
            count = 5,
            useUppercase = false,
            useLowercase = false,
            useNumbers = false,
            useSymbols = true,
            selectedSymbols = emptySet(),
            customSymbols = ""
        )
        
        assertFalse(config.isValid())
    }
    
    @Test
    fun `文字種が未選択で空文字列を返す`() {
        val config = PasswordConfig(
            useUppercase = false,
            useLowercase = false,
            useNumbers = false,
            useSymbols = false
        )
        
        assertEquals("", config.getCharacterSet())
    }
    
    @Test
    fun `記号選択したが選択記号もカスタム記号もない場合空文字列を返す`() {
        val config = PasswordConfig(
            useUppercase = false,
            useLowercase = false,
            useNumbers = false,
            useSymbols = true,
            selectedSymbols = emptySet(),
            customSymbols = ""
        )
        
        assertEquals("", config.getCharacterSet())
    }
    
    @Test
    fun `全ての文字種で結合した文字セットを返す`() {
        val config = PasswordConfig(
            useUppercase = true,
            useLowercase = true,
            useNumbers = true,
            useSymbols = true,
            selectedSymbols = setOf("!", "@")
        )
        
        val charset = config.getCharacterSet()
        assertTrue(charset.contains("A"))
        assertTrue(charset.contains("Z"))
        assertTrue(charset.contains("a"))
        assertTrue(charset.contains("z"))
        assertTrue(charset.contains("0"))
        assertTrue(charset.contains("9"))
        assertTrue(charset.contains("!"))
        assertTrue(charset.contains("@"))
    }
    
    @Test
    fun `選択記号のみで正しい記号を返す`() {
        val config = PasswordConfig(
            selectedSymbols = setOf("!", "@", "#"),
            customSymbols = ""
        )
        
        val allSymbols = config.getAllSymbols()
        assertEquals(3, allSymbols.length)
        assertTrue(allSymbols.contains("!"))
        assertTrue(allSymbols.contains("@"))
        assertTrue(allSymbols.contains("#"))
    }
    
    @Test
    fun `カスタム記号のみで正しい記号を返す`() {
        val config = PasswordConfig(
            selectedSymbols = emptySet(),
            customSymbols = "xyz"
        )
        
        val allSymbols = config.getAllSymbols()
        assertEquals(3, allSymbols.length)
        assertTrue(allSymbols.contains("x"))
        assertTrue(allSymbols.contains("y"))
        assertTrue(allSymbols.contains("z"))
    }
    
    @Test
    fun `重複カスタム記号がある場合重複を除去する`() {
        val config = PasswordConfig(
            selectedSymbols = emptySet(),
            customSymbols = "aabbcc"
        )
        
        val allSymbols = config.getAllSymbols()
        assertEquals(3, allSymbols.length)
        assertTrue(allSymbols.contains("a"))
        assertTrue(allSymbols.contains("b"))
        assertTrue(allSymbols.contains("c"))
    }
    
    @Test
    fun `選択記号とカスタム記号が重複する場合重複を除去する`() {
        val config = PasswordConfig(
            selectedSymbols = setOf("@", "#"),
            customSymbols = "@#xyz"
        )
        
        val allSymbols = config.getAllSymbols()
        assertEquals(5, allSymbols.length)
        assertTrue(allSymbols.contains("@"))
        assertTrue(allSymbols.contains("#"))
        assertTrue(allSymbols.contains("x"))
        assertTrue(allSymbols.contains("y"))
        assertTrue(allSymbols.contains("z"))
    }
    
    @Test
    fun `空の入力で空文字列を返す`() {
        val config = PasswordConfig(
            selectedSymbols = emptySet(),
            customSymbols = ""
        )
        
        val allSymbols = config.getAllSymbols()
        assertTrue(allSymbols.isEmpty())
    }
    
    @Test
    fun `文字セットの文字順序の一貫性を維持する`() {
        val config = PasswordConfig(
            useUppercase = true,
            useLowercase = true,
            useNumbers = true,
            useSymbols = false
        )
        
        val charset1 = config.getCharacterSet()
        val charset2 = config.getCharacterSet()
        assertEquals(charset1, charset2)
    }
    
    @Test
    fun `Unicodeカスタム記号で文字セットを取得する`() {
        val config = PasswordConfig(
            useUppercase = false,
            useLowercase = false,
            useNumbers = false,
            useSymbols = true,
            customSymbols = "αβγ"
        )
        
        val charset = config.getCharacterSet()
        assertTrue(charset.contains("α"))
        assertTrue(charset.contains("β"))
        assertTrue(charset.contains("γ"))
    }
    
    @Test
    fun `非常に大きな値でisValidをテストする`() {
        val config = PasswordConfig(
            length = Int.MAX_VALUE,
            count = Int.MAX_VALUE,
            useUppercase = true
        )
        
        assertFalse(config.isValid())
    }
    
    @Test
    fun `標準乱数アルゴリズムがデフォルトで設定される`() {
        val config = PasswordConfig()
        
        assertEquals(RandomAlgorithm.PSEUDO_RANDOM, config.randomAlgorithm)
    }
    
    @Test
    fun `暗号学的乱数アルゴリズムを設定できる`() {
        val config = PasswordConfig(
            randomAlgorithm = RandomAlgorithm.CRYPTOGRAPHICALLY_SECURE
        )
        
        assertEquals(RandomAlgorithm.CRYPTOGRAPHICALLY_SECURE, config.randomAlgorithm)
    }
    
    @Test
    fun `ハードウェア乱数アルゴリズムを設定できる`() {
        val config = PasswordConfig(
            randomAlgorithm = RandomAlgorithm.HARDWARE_RANDOM
        )
        
        assertEquals(RandomAlgorithm.HARDWARE_RANDOM, config.randomAlgorithm)
    }
}