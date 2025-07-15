package com.example.voote.view.kyc.address

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.voote.ui.components.Logo
import com.example.voote.ui.components.Text
import com.example.voote.viewModel.AuthViewModel
import com.example.voote.viewModel.KycViewModel
import com.example.voote.viewModel.UserViewModel


@Composable
fun AddressScreen(authManager: AuthViewModel, userViewModel: UserViewModel, kycViewModel: KycViewModel, navController: NavController) {

    Scaffold {
            innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Logo()
            Text(
                text = "Verify Address",
                fontSize = 30,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Provide your current residential address",
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
                AddressForm()
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                AddressButton(
                    authManager,
                    userViewModel,
                    kycViewModel,
                    navController
                )
            }
        }
    }
}
