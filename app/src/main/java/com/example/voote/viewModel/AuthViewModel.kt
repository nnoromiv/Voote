package com.example.voote.viewModel

import androidx.lifecycle.ViewModel
import com.example.voote.contract.Voote
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthViewModel : ViewModel() {
    private val _currentUser = MutableStateFlow(FirebaseAuth.getInstance().currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    // Listen to auth state changes and update _currentUser accordingly
    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        _currentUser.value = firebaseAuth.currentUser
    }

    val auth = FirebaseAuth.getInstance()

    init {
        auth.addAuthStateListener(authStateListener)
    }

    override fun onCleared() {
        super.onCleared()
        auth.removeAuthStateListener(authStateListener)
    }

    fun isLoggedIn(): Boolean = currentUser.value != null

    fun userUid(): String? = currentUser.value?.uid

    private val _contract = MutableStateFlow<Voote?>(null)
    val contract: StateFlow<Voote?> = _contract.asStateFlow()

    fun setContract(contract: Voote?) {
        _contract.value = contract
    }

    fun signOut() {
        auth.signOut()
    }
}
