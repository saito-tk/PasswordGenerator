package com.saitotk.passwordgenerator.domain.model

data class GeneratedPassword(
    val value: String,
    val id: Long = kotlin.random.Random.nextLong(),
    val hasFallback: Boolean = false
)