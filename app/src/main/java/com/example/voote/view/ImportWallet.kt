package com.example.voote.view

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.voote.ui.components.Logo
import com.example.voote.ui.components.PrimaryButton
import com.example.voote.ui.components.TextField
import com.example.voote.viewModel.WalletViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.voote.firebase.data.STATUS
import com.example.voote.navigation.RouteMain
import com.example.voote.navigation.RouteStatus
import com.example.voote.navigation.toJson
import com.example.voote.ui.components.Loader
import com.example.voote.utils.helpers.isValidMnemonic
import com.example.voote.wallet.WalletManager

@Composable
fun ImportWalletScreen(walletViewModel: WalletViewModel, navController: NavController) {

    val isLoading = remember { mutableStateOf(false) }
    val mnemonic by walletViewModel.mnemonic.collectAsState()
    val context = LocalContext.current

    val walletManager = WalletManager(context)

    fun handleImport() {
        isLoading.value = true

        if(mnemonic.isEmpty()) {
            isLoading.value = false
            return
        }

        if(!isValidMnemonic(mnemonic)) {
            Toast.makeText(context, "Invalid mnemonic", Toast.LENGTH_SHORT).show()
            isLoading.value = false
            return
        }

        val walletData = walletManager.importWallet(mnemonic)

        if(walletData == null) {
            Toast.makeText(context, "Error importing wallet", Toast.LENGTH_SHORT).show()
            isLoading.value = false
            return
        }

        walletViewModel.setWalletData(walletData)

        Toast.makeText(context, "Wallet imported", Toast.LENGTH_SHORT).show()
        isLoading.value = false

        navController.navigate(RouteStatus(
            status = STATUS.SUCCESS,
            nextScreen = RouteMain.toJson()
        ))
    }

    val errorExist = mnemonic.isEmpty()  || !isValidMnemonic(mnemonic)

    Scaffold { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .padding(innerPadding)
                .padding(10.dp)
                .fillMaxSize()
        ) {
            Logo()

            Text(
                text = "Import a Wallet",
                fontSize = 40.sp,
                modifier = Modifier.padding(16.dp),
                fontWeight = FontWeight.Bold
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                TextField(
                    value = mnemonic,
                    onValueChange = {
                        walletViewModel.setMnemonic(it)
                    },
                    label = { Text("Mnemonic") },
                    modifier = Modifier.height(100.dp)
                )
            }

            if(isLoading.value) {
                Loader()
            } else {
                PrimaryButton(
                    text = "Import",
                    onClick = { handleImport() },
                    enabled = !errorExist,
                )
            }
        }
    }
}