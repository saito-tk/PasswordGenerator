package com.saitotk.passwordgenerator.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PasswordConfigTest {
    
    @Test
    fun `isValid returns true for valid config`() {
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
    fun `isValid returns false for length less than 4`() {
        val config = PasswordConfig(
            length = 3,
            count = 5,
            useUppercase = true
        )
        
        assertFalse(config.isValid())
    }
    
    @Test
    fun `isValid returns false for length greater than 100 million`() {
        val config = PasswordConfig(
            length = 100_000_001,
            count = 5,
            useUppercase = true
        )
        
        assertFalse(config.isValid())
    }
    
    @Test
    fun `isValid returns true for length at maximum limit`() {
        val config = PasswordConfig(
            length = 100_000_000,
            count = 5,
            useUppercase = true
        )
        
        assertTrue(config.isValid())
    }
    
    @Test
    fun `isValid returns false for count less than 1`() {
        val config = PasswordConfig(
            length = 8,
            count = 0,
            useUppercase = true
        )
        
        assertFalse(config.isValid())
    }
    
    @Test
    fun `isValid returns false for count greater than 25`() {
        val config = PasswordConfig(
            length = 8,
            count = 26,
            useUppercase = true
        )
        
        assertFalse(config.isValid())
    }
    
    @Test
    fun `isValid returns false when no character types selected`() {
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
    fun `getCharacterSet returns correct characters for uppercase`() {
        val config = PasswordConfig(
            useUppercase = true,
            useLowercase = false,
            useNumbers = false,
            useSymbols = false
        )
        
        assertEquals("ABCDEFGHIJKLMNOPQRSTUVWXYZ", config.getCharacterSet())
    }
    
    @Test
    fun `getCharacterSet returns correct characters for lowercase`() {
        val config = PasswordConfig(
            useUppercase = false,
            useLowercase = true,
            useNumbers = false,
            useSymbols = false
        )
        
        assertEquals("abcdefghijklmnopqrstuvwxyz", config.getCharacterSet())
    }
    
    @Test
    fun `getCharacterSet returns correct characters for numbers`() {
        val config = PasswordConfig(
            useUppercase = false,
            useLowercase = false,
            useNumbers = true,
            useSymbols = false
        )
        
        assertEquals("0123456789", config.getCharacterSet())
    }
    
    @Test
    fun `getCharacterSet returns correct characters for symbols`() {
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
    fun `getCharacterSet combines all selected character types`() {
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
    fun `getAllSymbols includes both selected and custom symbols`() {
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
}