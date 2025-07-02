package com.saitotk.passwordgenerator.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals

class RandomAlgorithmTest {
    
    @Test
    fun `標準乱数の表示名が正しい`() {
        assertEquals("標準乱数", RandomAlgorithm.PSEUDO_RANDOM.displayName)
    }
    
    @Test
    fun `暗号学的乱数の表示名が正しい`() {
        assertEquals("暗号学的乱数(CSPRNG)", RandomAlgorithm.CRYPTOGRAPHICALLY_SECURE.displayName)
    }
    
    @Test
    fun `ハードウェア乱数の表示名が正しい`() {
        assertEquals("ハードウェア乱数", RandomAlgorithm.HARDWARE_RANDOM.displayName)
    }
    
    @Test
    fun `標準乱数の説明が正しい`() {
        assertEquals("高速で一般的な用途に適した疑似乱数生成器", RandomAlgorithm.PSEUDO_RANDOM.description)
    }
    
    @Test
    fun `暗号学的乱数の説明が正しい`() {
        assertEquals("暗号学的に安全な乱数生成器。セキュリティが重要な場合に推奨", RandomAlgorithm.CRYPTOGRAPHICALLY_SECURE.description)
    }
    
    @Test
    fun `ハードウェア乱数の説明が正しい`() {
        assertEquals("ハードウェアエントロピーを使用した真の乱数生成器（利用可能な場合）", RandomAlgorithm.HARDWARE_RANDOM.description)
    }
    
    @Test
    fun `すべてのアルゴリズムが列挙されている`() {
        val algorithms = RandomAlgorithm.values()
        assertEquals(3, algorithms.size)
        assertEquals(RandomAlgorithm.PSEUDO_RANDOM, algorithms[0])
        assertEquals(RandomAlgorithm.CRYPTOGRAPHICALLY_SECURE, algorithms[1])
        assertEquals(RandomAlgorithm.HARDWARE_RANDOM, algorithms[2])
    }
}