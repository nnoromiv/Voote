package com.example.voote.view.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PowerSettingsNew
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.voote.ThisApplication
import com.example.voote.blockchain.Connector
import com.example.voote.model.data.KycData
import com.example.voote.model.data.UserData
import com.example.voote.ui.components.BottomNavigation
import com.example.voote.ui.components.CountDown
import com.example.voote.ui.components.ProfileBar
import com.example.voote.ui.components.Text
import com.example.voote.view.profile.modals.LogOutModal
import com.example.voote.viewModel.AuthViewModel
import com.example.voote.viewModel.KycViewModel
import com.example.voote.viewModel.UserViewModel
import com.example.voote.viewModel.WalletViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen( authManager: AuthViewModel, kycViewModel: KycViewModel, userViewModel: UserViewModel, walletViewModel: WalletViewModel, navController: NavController) {

    val userData by userViewModel.userData.collectAsState()
    val kycData by kycViewModel.kycData.collectAsState()
    val walletData by walletViewModel.walletData.collectAsState()

    val connector = Connector(walletViewModel)

    val logOutSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var logOutShowSheet by remember { mutableStateOf(false) }

    val name = userData?.firstName + " " + userData?.lastName
    val userName = (userData?.lastName + userData?.walletId?.substring(0, 4)).lowercase()
    val walletBalance = remember { mutableDoubleStateOf(0.0) }

    val context = LocalContext.current
    val coroutineScope = (context.applicationContext as ThisApplication).appScope



    fun loadData() {
        val address = walletData?.address
        if (address != null) {
            coroutineScope.launch {
                val result = connector.getWalletBalance(address)
                withContext(Dispatchers.Main) {
                    walletBalance.doubleValue = result?.toDouble() ?: 0.0
                }
            }
        }
    }


    LaunchedEffect(true) {
        loadData()
    }

    fun handleLogOutModal() {
        logOutShowSheet = true
        coroutineScope.launch {
            logOutSheetState.show()
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
            verticalArrangement = Arrangement.spacedBy(30.dp)
        ){
            ProfileBar(
                name,
                userName,
                imageVector = Icons.Outlined.PowerSettingsNew,
                onClick = { handleLogOutModal() }
            )

            CountDown (
                text = "Wallet",
                "${walletBalance.doubleValue} SETH",
                contractAddress = walletData?.address
            )

            VerificationInformation(
                userData,
                kycData
            )

//            PrimaryButton(
//                text = "Back Up Wallet",
//                onClick = { /*TODO*/ },
//            )

        }

        if (logOutShowSheet) {
            LogOutModal (
                authManager,
                kycViewModel,
                userViewModel,
                navController,
                sheetState = logOutSheetState,
                onDismissRequest = {
                    logOutShowSheet = false
                }
            )
        }
    }
}

@Composable
fun DisplayInformation(title: String, value: String) {
    Row {
        Text(
            text = title,
            fontSize = 16,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = value,
            fontSize = 16,
            softWrap = true
        )
    }
}

@Composable
fun VerificationInformation(userData: UserData?, kycData: KycData?) {
    HorizontalDivider()

    if (userData != null) {
        DisplayInformation(
            title = "Email",
            value = userData.email
        )

        DisplayInformation(
            title = "Phone Number",
            value = userData.phoneNumber
        )
    }

    if (kycData != null) {
        DisplayInformation(
            title = "Passport",
            value = kycData.passportNumber
        )

        DisplayInformation(
            title = "Driver's License",
            value = kycData.driverLicenceNumber
        )
    }

    HorizontalDivider()
}
