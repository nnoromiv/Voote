package com.example.voote.model.data

import com.google.firebase.auth.FirebaseUser

data class SignUpData(
    val user: FirebaseUser?,
)

data class LoginData(
    val uid: String?
)
