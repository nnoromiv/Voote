package com.example.voote.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.voote.firebase.data.AuditLogEntry
import com.example.voote.firebase.user.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class HistoryViewModel(
    private val userViewModel: UserViewModel
): ViewModel() {

    val auditLogs = MutableStateFlow(emptyList<AuditLogEntry>())

    val isLoading = MutableStateFlow(false)

    fun loadFirst50AuditLogs(isLoggedIn: Boolean) {
        if (!isLoggedIn) return

        viewModelScope.launch {
            isLoading.value = true
            val userData = userViewModel.userData.value
            val user = User(userData?.uid.toString())

            try {
                val logs = user.getFirst50AuditLogs()
                Log.d("HistoryScreen", "Audit logs: $logs")
                auditLogs.value = logs
            } catch (e: Exception) {
                e.printStackTrace()
                auditLogs.value = emptyList()
            } finally {
                isLoading.value = false
            }
        }
    }
}