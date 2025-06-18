package com.example.voote.model.data

data class UserData (
    val createdAt: Long = 0L,
    val email: String = "",
    val firstName: String = "",
    val isPhoneVerified: Boolean = false,
    val lastName: String = "",
    val phoneNumber: String = "",
    val role: String = "",
    val uid: String = "",
    val walletId: String = ""
)
