package com.firebasevapecounter.model

data class User(
    var userId: String? = "",
    var name: String? = "",
    var email: String? = "",
    var phone: String? = "",
    var totalCount: Int = 0,
    var currentCount: Int = 0,
    var admin: Boolean = false,
    var token: String ?= ""
) : java.io.Serializable