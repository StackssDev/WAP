package com.firebasevapecounter.model

data class User(
    val userId: String? = "",
    val name: String? = "",
    val email: String? = "",
    var totalCount: Int = 0,
    var currentCount: Int = 0,
    val admin: Boolean = false,
    var token: String ?= ""
) : java.io.Serializable