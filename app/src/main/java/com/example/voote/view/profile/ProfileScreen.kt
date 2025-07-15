package com.example.voote.view.profile

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PowerSettingsNew
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.voote.navigation.RouteLogin
import com.example.voote.ui.components.BottomNavigation
import com.example.voote.ui.components.ProfileBar
import com.example.voote.viewModel.AuthViewModel
import com.example.voote.viewModel.KycViewModel
import com.example.voote.viewModel.UserViewModel
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen( authManager: AuthViewModel, kycViewModel: KycViewModel, userViewModel: UserViewModel, navController: NavController) {

    val userData by userViewModel.userData.collectAsState()

    val name = userData?.firstName + " " + userData?.lastName
    val userName = (userData?.lastName + userData?.walletId?.substring(0, 4)).lowercase()
    val coroutineScope = rememberCoroutineScope()

    fun handleLogout() {
        Log.d("Logout", "Logging out")
        coroutineScope.launch {
            authManager.signOut()
            kycViewModel.cleanKycData()
            userViewModel.cleanUserData()

            // Delete wallet file

            navController.navigate(RouteLogin)
        }
    }

    Scaffold (
        bottomBar = {
            BottomNavigation(navController = navController)
        }
    ) {
            innerPadding ->
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            ProfileBar(
                name,
                userName,
                imageVector = Icons.Outlined.PowerSettingsNew,
                onClick = { handleLogout() }
            )
        }
    }
}