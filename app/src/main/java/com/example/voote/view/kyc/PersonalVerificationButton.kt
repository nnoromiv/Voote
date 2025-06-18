package com.example.voote.view.kyc

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.voote.firebase.auth.Verification
import com.example.voote.navigation.Login
import com.example.voote.navigation.PassportVerification
import com.example.voote.navigation.homeObject
import com.example.voote.ui.components.COutlinedButton
import com.example.voote.ui.components.CTextButton
import com.example.voote.ui.components.PrimaryButton
import com.example.voote.ui.components.Text
import com.example.voote.utils.helpers.generateHMAC
import com.example.voote.utils.helpers.getOrCreateHMACKey
import com.example.voote.utils.helpers.verifyHMAC
import com.example.voote.viewModel.AuthViewModel
import com.example.voote.viewModel.UserViewModel
import com.example.voote.viewModel.WalletViewModel
import com.example.voote.wallet.WalletManager
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun PersonalVerificationButton(context: Context, authViewModel: AuthViewModel, userViewModel: UserViewModel,  navController: NavController) {

    val walletViewModel : WalletViewModel = viewModel()

    val isLoggedIn = authViewModel.isLoggedIn()
    val uid = authViewModel.userUid().toString()
    val coroutineScope = rememberCoroutineScope()
    val secretKey = getOrCreateHMACKey()

    val userData by userViewModel.userData.collectAsState()

    fun saveProgressToDB(address: String, isSkipped: Boolean) {
        val walletId = generateHMAC(uid + address, secretKey)

        Verification().saveWalletToDocument(walletId) { walletId ->
            walletViewModel.setAddress(walletId)
        }
        Toast.makeText(context, "Saving", Toast.LENGTH_SHORT).show()

        if(isSkipped){
            navController.navigate(homeObject)
        } else {
            navController.navigate(PassportVerification)
        }
    }

    fun beginVerification(isSkipped: Boolean) {
        if (!isLoggedIn) {
            Toast.makeText(context, "Log in to continue", Toast.LENGTH_SHORT).show()
            navController.navigate(Login)
            return
        }

        val walletContext = WalletManager(context)
        val existingWalletId = userData?.walletId

        val walletFiles = File(context.filesDir, "wallets").listFiles()
        val walletFile = walletFiles?.firstOrNull()

        if (walletFile != null) {
            val hasWalletStored = walletContext.getWalletContent(walletFile.name)

            if (hasWalletStored != null) {
                val walletIdIsStillValid = verifyHMAC(uid + hasWalletStored.address, existingWalletId.toString(), secretKey)

                if(walletIdIsStillValid) {
                    if(isSkipped){
                        navController.navigate(homeObject)
                    } else {
                        navController.navigate(PassportVerification)
                    }

                    return
                }
            }
        }

        val (newAddress, _) = walletContext.createWallet()

        if(!existingWalletId.isNullOrBlank()){
            coroutineScope.launch {
                try {
                    val walletId = generateHMAC(uid + newAddress, secretKey)

                    Verification().renameDocument(userData?.walletId!!, walletId)
                    Toast.makeText(context, "Saving", Toast.LENGTH_SHORT).show()
                    navController.navigate(PassportVerification)

                } catch (e: Exception) {
                    Log.e("beginVerification", "Rename failed", e)
                    Toast.makeText(context, "Failed to rename wallet", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            saveProgressToDB(newAddress, isSkipped)
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PrimaryButton(
            text = "Begin Verification",
            onClick = {
                beginVerification(false)
            },
            modifier = Modifier.padding(vertical = 10.dp)
        )

        COutlinedButton(
            text = "Skip",
            onClick = {
                beginVerification(true)
            },
            modifier = Modifier.padding(vertical = 10.dp)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "We have a ",
                fontSize = 18,
                fontWeight = FontWeight.Normal,
            )
            CTextButton(
                text = "Privacy Policy",
                onClick = {},
            )
        }
    }
}
