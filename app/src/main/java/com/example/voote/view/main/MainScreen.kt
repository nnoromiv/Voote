package com.example.voote.view.main

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.voote.blockchain.Connector
import com.example.voote.viewModel.AuthViewModel
import com.example.voote.viewModel.WalletViewModel

@Composable
fun MainScreen(authManager: AuthViewModel, walletViewModel: WalletViewModel, navController: NavController) {

    val walletData = walletViewModel.walletData.collectAsState()
    val connector = Connector(walletViewModel)

    LaunchedEffect(Unit) {
        val value = walletData.value

        when {
            value == null -> {
                return@LaunchedEffect
            }

            value.privateKey.isBlank() -> {
                return@LaunchedEffect
            }

            else -> {
                try {
                    val contract = connector.connectContract(value.privateKey)

                    if (contract == null) {
                        Log.e("Connector", "Failed to connect: Contract is null.")
                    }

                    authManager.setContract(contract)

                } catch (e: Exception) {
                    Log.e("Connector", "Exception during contract connection", e)
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(0.dp)
            .background(Color(0xFFFFFFFB)),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .weight(2f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = 200.dp))
                .background(
                    color = Color.White,
                ),
        ) {
            ActivityImages()
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) {
            MainButton(walletViewModel, navController)
        }
    }
}
