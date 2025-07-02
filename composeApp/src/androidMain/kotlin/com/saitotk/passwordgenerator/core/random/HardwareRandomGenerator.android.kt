package com.saitotk.passwordgenerator.core.random

import java.security.SecureRandom

actual class HardwareRandomGenerator : RandomGenerator {
    private val secureRandom = SecureRandom.getInstanceStrong()
    private var _hasFallbackOccurred = false
    
    actual override fun nextInt(bound: Int): Int {
        return try {
            // 強力なSecureRandomインスタンスを使用（ハードウェアエントロピー利用）
            secureRandom.nextInt(bound)
        } catch (e: Exception) {
            // フォールバック: 通常のSecureRandomを使用
            _hasFallbackOccurred = true
            SecureRandom().nextInt(bound)
        }
    }
    
    actual override val hasFallbackOccurred: Boolean
        get() = _hasFallbackOccurred
}