package com.firebasevapecounter.model

data class OrderHistory(
    val created: Long = 0,
    val name: String = "",
    val userId: String = "",
    val currentCount: Int = 0,
)