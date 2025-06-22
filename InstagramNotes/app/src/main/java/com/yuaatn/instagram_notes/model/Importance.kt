package com.yuaatn.instagram_notes.model

enum class Importance {
    LOW, NORMAL, HIGH;

    fun getUiName(): String = when (this) {
        LOW -> "\uD83D\uDE34 Неважная"
        NORMAL -> "\uD83D\uDE4F Обычная"
        HIGH -> "❗\uFE0F Сверхважная"
    }
}