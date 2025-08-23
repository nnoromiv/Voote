package com.example.voote.view.kyc

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.voote.ui.components.Logo
import com.example.voote.ui.components.Text
import com.example.voote.viewModel.AuthViewModel
import com.example.voote.viewModel.UserViewModel
import androidx.compose.runtime.getValue
import com.example.voote.firebase.auth.Verification
import com.example.voote.firebase.data.STATUS
import com.example.voote.navigation.RouteSignup
import com.example.voote.viewModel.WalletViewModel


@Composable
fun PersonalVerificationScreen(authViewModel: AuthViewModel, userViewModel: UserViewModel, walletViewModel: WalletViewModel, navController: NavController) {

    val context = LocalContext.current
    val userData by userViewModel.userData.collectAsState()
    val verification = Verification(authViewModel)

    LaunchedEffect(Unit) {
        if (userData == null) {
            navController.navigate(RouteSignup)
        }

        val walletId = userData?.walletId

        val result = verification.checkOrSaveWalletId(walletId, walletViewModel)

        if(result.status == STATUS.ERROR){
            Log.e("PersonalVerification", "Error checking or saving wallet ID", Exception(result.message))
            return@LaunchedEffect
        }

        Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
    }

    Scaffold {
            innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding).padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Logo()

            Text(
                text = "Personal Information",
                fontSize = 30,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Verification",
                fontSize = 30,
                fontWeight = FontWeight.Bold,
            )

            Text(
                text = "A valid government issued ID is required to verify your account",
                fontSize = 16,
                fontWeight = FontWeight.Normal,
                softWrap = true,
            )

            HorizontalDivider(
                color = Color(0x401B1B1B),
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                PersonalVerificationText()
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                PersonalVerificationButton(
                    context,
                    authViewModel,
                    navController
                )
            }
        }
    }
}
