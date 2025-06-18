package com.example.voote.view.kyc

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import com.example.voote.ui.components.Logo
import com.example.voote.ui.components.PrimaryButton
import com.example.voote.ui.components.Text
import com.example.voote.utils.helpers.generateHMAC
import com.example.voote.utils.helpers.getOrCreateHMACKey
import com.example.voote.viewModel.AuthViewModel
import com.example.voote.viewModel.UserViewModel
import com.example.voote.viewModel.WalletViewModel
import com.example.voote.wallet.WalletManager
import com.google.firebase.auth.FirebaseUser
import java.io.File
import androidx.compose.runtime.getValue

@Composable
fun PersonalVerificationScreen(navController: NavController, authViewModel: AuthViewModel, userViewModel: UserViewModel) {

    val context = LocalContext.current

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
                    userViewModel,
                    navController
                )
            }
        }
    }
}
