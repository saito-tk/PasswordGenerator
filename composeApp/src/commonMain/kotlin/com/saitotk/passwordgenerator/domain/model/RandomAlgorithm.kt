package com.saitotk.passwordgenerator.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class RandomAlgorithm(val displayName: String, val description: String) {
    PSEUDO_RANDOM(
        displayName = "標準乱数",
        description = "高速で一般的な用途に適した疑似乱数生成器"
    ),
    CRYPTOGRAPHICALLY_SECURE(
        displayName = "暗号学的乱数(CSPRNG)",
        description = "暗号学的に安全な乱数生成器。セキュリティが重要な場合に推奨"
    ),
    HARDWARE_RANDOM(
        displayName = "ハードウェア乱数",
        description = "ハードウェアエントロピーを使用した真の乱数生成器（利用可能な場合）"
    )
}