package com.korniiienko.model

enum class Importance {
    LOW,
    BASIC,
    IMPORTANT;

    override fun toString(): String = when (this) {
        LOW -> "\uD83D\uDE34 Неважная"
        BASIC -> "\uD83D\uDE4F Обычная"
        IMPORTANT -> "❗\uFE0F Сверхважная"
    }
}

