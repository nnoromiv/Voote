package com.example.voote.view.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.example.voote.ui.components.BottomNavigation
import com.example.voote.ui.components.CountDown
import com.example.voote.ui.components.ElectionBlock
import com.example.voote.ui.components.KYCReminder
import com.example.voote.ui.components.ProfileBar
import com.example.voote.ui.components.TextField
import com.example.voote.utils.Constants
import com.example.voote.viewModel.KycViewModel
import com.example.voote.viewModel.UserViewModel
import com.example.voote.wallet.WalletManager
import java.io.File

@Composable
fun HomeScreen(navController: NavController, userViewModel: UserViewModel, kycViewModel: KycViewModel) {
    var searchValue by remember { mutableStateOf("") }

    val kycCompletionPercentage by kycViewModel.kycCompletionPercentage.collectAsState()
    var contractAddress by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    val userData by userViewModel.userData.collectAsState()

    val name = userData?.firstName + " " + userData?.lastName
    val username = (userData?.lastName + userData?.walletId?.substring(0, 4)).lowercase()

    LaunchedEffect(Unit) {
        val walletContext = WalletManager(context)

        val walletFiles = File(context.filesDir, "wallets").listFiles()
        val walletFile = walletFiles?.firstOrNull()

        if (walletFile != null) {
            val hasWalletStored = walletContext.getWalletContent(walletFile.name)

            if (hasWalletStored != null) {
                contractAddress = hasWalletStored.address
            }
        }
    }

    Scaffold (
        bottomBar = {
            BottomNavigation(navController)
        }
    ) {
            innerPadding ->
        Column (
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(10.dp)
                .verticalScroll(rememberScrollState())
        ) {
            ProfileBar(
                name,
                username,
                useInitials = true,
                userImageUri = Constants().imageUrl.toUri()
            )
            KYCReminder(
                navController,
                kycCompletionPercentage
            )

            CountDown(
                text = "Presidential Election",
                totalTime = 1000,
                contractAddress = contractAddress
            ).Card()

            SearchForElection(
                searchValue = searchValue,
                onSearchValueChange = { searchValue = it }
            )

            ElectionBlock(navController)

            ElectionBlock(navController)
      }
    }
}

@Composable
fun SearchForElection(
    searchValue: String,
    onSearchValueChange: (String) -> Unit
) {
    TextField(
        value = searchValue,
        onValueChange = onSearchValueChange,
        label = {
            Text(
            "Search for elections",
        ) },
        trailingIcon = {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        },
    )
}

