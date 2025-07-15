package com.example.voote.view.main

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.voote.R
import com.example.voote.navigation.RouteLogin
import com.example.voote.navigation.RouteSignup
import com.example.voote.ui.components.COutlinedButton
import com.example.voote.ui.components.PrimaryButton
import com.example.voote.utils.Constants
import com.example.voote.viewModel.WalletViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.example.voote.firebase.data.Status
import com.example.voote.navigation.RouteImportWallet
import com.example.voote.navigation.RouteMain
import com.example.voote.navigation.RouteStatus
import com.example.voote.navigation.toJson
import com.example.voote.ui.components.Loader
import com.example.voote.wallet.WalletManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MainButton(walletViewModel: WalletViewModel, navController: NavController) {

    val walletData by walletViewModel.walletData.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val constants = Constants()

    val isLoading = remember { mutableStateOf(false) }
    val context = LocalContext.current

    val walletManager = WalletManager(context)

    fun handleCreateWallet() {
        isLoading.value = true

        coroutineScope.launch {
            delay(2000)
            val walletData = walletManager.createWallet()

            if(walletData == null) {
                Toast.makeText(context, "Error creating wallet", Toast.LENGTH_SHORT).show()
                isLoading.value = false
                return@launch
            }

            walletViewModel.setWalletData(walletData)

            Toast.makeText(context, "Wallet Created", Toast.LENGTH_SHORT).show()
            isLoading.value = false

            navController.navigate(RouteStatus(
                status = Status.SUCCESS,
                nextScreen = RouteMain.toJson()
            ))
        }
    }

    Column(
        modifier = constants.cFullSizeModifier.padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = stringResource(R.string.easy_online_election),
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = constants.centuryFontFamily,
            color = Color.Black,
        )
        Text(
            text = stringResource(R.string.main_screen_sub_text),
            fontSize = 16.sp,
            fontFamily = constants.centuryFontFamily,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        if(walletData == null) {
            if(isLoading.value) {
                Loader()
            } else {
                PrimaryButton(
                    text = "Create a Wallet",
                    onClick = {
                        handleCreateWallet()
                    },
                )
            }

            COutlinedButton(
                text = "Import a Wallet",
                enabled = !isLoading.value,
                onClick = {
                    navController.navigate(RouteImportWallet)
                }
            )
        } else {
            PrimaryButton(
                text = stringResource(R.string.login),
                onClick = {
                    navController.navigate(RouteLogin)
                },
            )

            COutlinedButton(
                text = stringResource(R.string.sign_up),
                onClick = {
                    navController.navigate(RouteSignup)
                }
            )
        }
    }
}
