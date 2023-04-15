package com.firebasevapecounter.model

data class OrderHistory(
    val created: Long = 0,
    val name: String = "",
    val userId: String = "",
    val currentCount: Int = 0,
    var status: String = Status.PENDING
)


object Status {
    const val PENDING = "p"
    const val ACCEPTED = "a"
    const val REJECTED = "r"
}