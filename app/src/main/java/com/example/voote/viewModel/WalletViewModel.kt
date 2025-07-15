package com.example.voote.viewModel

import androidx.lifecycle.ViewModel
import com.example.voote.model.data.WalletData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class WalletViewModel: ViewModel() {
    private val _walletData = MutableStateFlow<WalletData?>(null)
    val walletData : StateFlow<WalletData?> = _walletData.asStateFlow()

    fun setWalletData(value: WalletData) {
        _walletData.value = value
    }

    private val _mnemonic = MutableStateFlow("")
    val mnemonic : StateFlow<String> = _mnemonic.asStateFlow()

    fun setMnemonic(value: String) {
        _mnemonic.value = value
    }

}