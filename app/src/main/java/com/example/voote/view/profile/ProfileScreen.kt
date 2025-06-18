package com.example.voote.view.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.example.voote.ui.components.BottomNavigation
import com.example.voote.ui.components.ProfileBar
import com.example.voote.utils.Constants
import com.example.voote.viewModel.UserViewModel

@Composable
fun ProfileScreen(navController: NavController, userViewModel: UserViewModel) {

    val userData by userViewModel.userData.collectAsState()

    val name = userData?.firstName + " " + userData?.lastName
    val username = (userData?.lastName + userData?.walletId?.substring(0, 4)).lowercase()

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
                username,
                useInitials = true,
                userImageUri = Constants().imageUrl.toUri()
            )
        }
    }
}