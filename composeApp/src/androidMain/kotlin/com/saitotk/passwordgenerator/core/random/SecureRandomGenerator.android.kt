package com.saitotk.passwordgenerator.core.random

import java.security.SecureRandom

actual class SecureRandomGenerator : RandomGenerator {
    private val secureRandom = SecureRandom()
    
    actual override fun nextInt(bound: Int): Int {
        return secureRandom.nextInt(bound)
    }
}