package com.example.voote.view.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.example.voote.ui.components.BottomNavigation
import com.example.voote.ui.components.ProfileBar
import com.example.voote.utils.Constants

@Composable
fun ProfileScreen(navController: NavController) {
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
                name = "Jane Doe",
                username = "janny",
                userImageUri = Constants().imageUrl.toUri()
            )
        }
    }
}