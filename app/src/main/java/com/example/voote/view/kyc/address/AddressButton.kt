package com.example.voote.view.kyc.address

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.voote.firebase.auth.Verification
import com.example.voote.firebase.data.Status
import com.example.voote.navigation.RouteFaceVerification
import com.example.voote.navigation.RouteHome
import com.example.voote.navigation.RouteStatus
import com.example.voote.navigation.toJson
import com.example.voote.ui.components.COutlinedButton
import com.example.voote.ui.components.Loader
import com.example.voote.ui.components.PrimaryButton
import com.example.voote.viewModel.AddressViewModel
import com.example.voote.viewModel.AuthViewModel
import com.example.voote.viewModel.KycViewModel
import com.example.voote.viewModel.UserViewModel
import kotlinx.coroutines.launch

@Composable
fun AddressButton(authManager: AuthViewModel, userViewModel: UserViewModel, kycViewModel: KycViewModel, navController: NavController) {

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val isLoading = remember { mutableStateOf(false) }
    val verification = Verification(authManager)

    val addressViewModel : AddressViewModel = viewModel()
    val country by addressViewModel.country.collectAsState()
    val state by addressViewModel.state.collectAsState()
    val city by addressViewModel.city.collectAsState()
    val street by addressViewModel.street.collectAsState()
    val postCode by addressViewModel.postCode.collectAsState()

    val kycData by kycViewModel.kycData.collectAsState()

    val walletId = userViewModel.userData.collectAsState().value?.walletId ?: ""

    val errorExist = country.isEmpty() || state.isEmpty() || city.isEmpty() || street.isEmpty() || postCode.isEmpty()


    fun handleSaveAddress() {
        val residentialAddress = "$postCode, $street, $city, $state, $country"

        isLoading.value = true

        if(residentialAddress.isEmpty()) {
            isLoading.value = false
            return
        }

        if(walletId.isEmpty()) {
            Toast.makeText(context, "Error saving address", Toast.LENGTH_SHORT).show()
            isLoading.value = false
            return
        }

        coroutineScope.launch {

            val success = verification.setResidentialAddress(residentialAddress, walletId)

            if(success.status == Status.ERROR) {
                navController.navigate(RouteStatus(
                    status = Status.ERROR,
                    nextScreen = ""
                ))

                isLoading.value = false
                return@launch
            }

            navController.navigate(RouteStatus(
                status = Status.SUCCESS,
                nextScreen = RouteFaceVerification("").toJson()
            ))

        }
    }

    val residentialAddressKycDataExist = kycData?.residentialAddress?.isNotEmpty() == true

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if(isLoading.value) {
            Loader()
        } else {
            if(residentialAddressKycDataExist) {
                COutlinedButton(
                    text = "Continue",
                    onClick = {
                        navController.navigate(RouteFaceVerification(
                            userImageUri = ""
                        ))
                    },
                    enabled = !isLoading.value,
                    modifier = Modifier.padding(vertical = 10.dp)
                )
            }

            PrimaryButton(
                text = if (residentialAddressKycDataExist) "Update" else "Continue",
                onClick = { handleSaveAddress() },
                enabled = !errorExist,
                modifier = Modifier.padding(vertical = 10.dp)
            )

            if(!residentialAddressKycDataExist) {
                COutlinedButton(
                    text = "Do this later",
                    onClick = {
                        navController.navigate(RouteHome)
                    },
                    enabled = !isLoading.value,
                    modifier = Modifier.padding(vertical = 10.dp)
                )
            }
        }

    }
}
