package com.example.voote.model.data

data class WalletData(
    val address: String,
    val mnemonic: String,
    val privateKey: String,
    val timestamp: String
)

data class WalletIdData(
    val walletId: String?,
    val privateKey: String? = null
)