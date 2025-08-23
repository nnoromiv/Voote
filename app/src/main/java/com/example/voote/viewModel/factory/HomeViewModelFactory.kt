package com.example.voote.viewModel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.voote.firebase.election.Election
import com.example.voote.viewModel.HomeViewModel
import com.example.voote.viewModel.UserViewModel
import com.example.voote.viewModel.WalletViewModel

class HomeViewModelFactory(
    private val election: Election,
    private val userViewModel: UserViewModel,
    private val walletViewModel: WalletViewModel
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(election, userViewModel, walletViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
