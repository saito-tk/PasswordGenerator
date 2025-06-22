package com.saitotk.passwordgenerator

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform