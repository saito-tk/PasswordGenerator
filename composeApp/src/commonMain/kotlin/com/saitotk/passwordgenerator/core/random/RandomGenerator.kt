package com.saitotk.passwordgenerator.core.random

import com.saitotk.passwordgenerator.domain.model.RandomAlgorithm

interface RandomGenerator {
    fun nextInt(bound: Int): Int
    val hasFallbackOccurred: Boolean
}

expect class SecureRandomGenerator() : RandomGenerator {
    override fun nextInt(bound: Int): Int
    override val hasFallbackOccurred: Boolean
}

expect class HardwareRandomGenerator() : RandomGenerator {
    override fun nextInt(bound: Int): Int
    override val hasFallbackOccurred: Boolean
}

class PseudoRandomGenerator : RandomGenerator {
    override fun nextInt(bound: Int): Int {
        return kotlin.random.Random.nextInt(bound)
    }
    
    override val hasFallbackOccurred: Boolean = false
}

object RandomGeneratorFactory {
    fun create(algorithm: RandomAlgorithm): RandomGenerator {
        return when (algorithm) {
            RandomAlgorithm.PSEUDO_RANDOM -> PseudoRandomGenerator()
            RandomAlgorithm.CRYPTOGRAPHICALLY_SECURE -> SecureRandomGenerator()
            RandomAlgorithm.HARDWARE_RANDOM -> HardwareRandomGenerator()
        }
    }
}