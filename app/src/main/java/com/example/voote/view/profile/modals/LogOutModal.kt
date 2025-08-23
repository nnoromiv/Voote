package com.example.voote.view.profile.modals

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.voote.ui.components.PrimaryButton
import com.example.voote.ui.components.Text
import androidx.navigation.NavController
import com.example.voote.navigation.RouteLogin
import com.example.voote.viewModel.AuthViewModel
import kotlinx.coroutines.launch
import com.example.voote.viewModel.KycViewModel
import com.example.voote.viewModel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogOutModal(authManager: AuthViewModel, kycViewModel: KycViewModel, userViewModel: UserViewModel, navController: NavController, sheetState: SheetState, onDismissRequest: () -> Unit) {

    val coroutineScope = rememberCoroutineScope()

    fun handleLogout() {
        coroutineScope.launch {
            authManager.signOut()
            kycViewModel.cleanKycData()
            userViewModel.cleanUserData()

            navController.navigate(RouteLogin)
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = Color.White,
    ) {
        Column(
            modifier = Modifier
                .padding(start =  10.dp, bottom = 10.dp, end = 10.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(15.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                "Are you sure you want to log out?",
                fontSize = 18,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.padding(top = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    PrimaryButton(
                        text = "No",
                        onClick = { onDismissRequest() },
                        colors = ButtonColors(
                            containerColor = Color(0xFF00BA11),
                            contentColor = Color.White,
                            disabledContainerColor = Color.Gray,
                            disabledContentColor = Color.White
                        )
                    )
                }

                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    PrimaryButton(
                        text = "Log Out",
                        onClick = { handleLogout() },
                        colors = ButtonColors(
                            containerColor = Color.Red,
                            contentColor = Color.White,
                            disabledContainerColor = Color.Gray,
                            disabledContentColor = Color.White
                        )
                    )
                }
            }
        }
    }
}
