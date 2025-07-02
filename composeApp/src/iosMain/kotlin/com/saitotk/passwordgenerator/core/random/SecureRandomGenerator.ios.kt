package com.saitotk.passwordgenerator.core.random

import kotlinx.cinterop.*
import platform.Security.*
import kotlin.random.Random

actual class SecureRandomGenerator : RandomGenerator {
    private var _hasFallbackOccurred = false
    
    actual override fun nextInt(bound: Int): Int {
        return try {
            // iOS SecRandomCopyBytesを使用してセキュアな乱数を生成
            val byteArray = ByteArray(4)
            byteArray.usePinned { pinned ->
                val result = SecRandomCopyBytes(kSecRandomDefault, 4u, pinned.addressOf(0))
                if (result == errSecSuccess) {
                    // バイト配列をIntに変換し、boundで剰余を取る
                    val randomInt = byteArray.fold(0) { acc, byte -> 
                        (acc shl 8) or (byte.toInt() and 0xFF) 
                    }
                    kotlin.math.abs(randomInt) % bound
                } else {
                    // フォールバック: 標準乱数を使用
                    _hasFallbackOccurred = true
                    Random.nextInt(bound)
                }
            }
        } catch (e: Exception) {
            // エラー時のフォールバック
            _hasFallbackOccurred = true
            Random.nextInt(bound)
        }
    }
    
    actual override val hasFallbackOccurred: Boolean
        get() = _hasFallbackOccurred
}