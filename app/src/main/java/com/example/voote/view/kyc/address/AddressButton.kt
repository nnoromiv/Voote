package com.example.voote.view.kyc.address

import android.util.Log
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
import com.example.voote.navigation.FaceVerification
import com.example.voote.navigation.homeObject
import com.example.voote.ui.components.COutlinedButton
import com.example.voote.ui.components.Loader
import com.example.voote.ui.components.PrimaryButton
import com.example.voote.viewModel.AddressViewModel
import com.example.voote.viewModel.KycViewModel
import com.example.voote.viewModel.UserViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AddressButton(navController: NavController, userViewModel: UserViewModel, kycViewModel: KycViewModel) {

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val isLoading = remember { mutableStateOf(false) }

    val addressViewModel : AddressViewModel = viewModel()
    val country by addressViewModel.country.collectAsState()
    val state by addressViewModel.state.collectAsState()
    val city by addressViewModel.city.collectAsState()
    val street by addressViewModel.street.collectAsState()
    val postCode by addressViewModel.postCode.collectAsState()

    val kycData by kycViewModel.kycData.collectAsState()

    val walletId = userViewModel.userData.collectAsState().value?.walletId ?: ""

    val errorExist = country.isEmpty() || state.isEmpty() || city.isEmpty() || street.isEmpty() || postCode.isEmpty()

    val residentialAddress = "$postCode, $street, $city, $state, $country"

    fun handleSaveAddress() {
        isLoading.value = true

        if(residentialAddress.isEmpty()) {
            isLoading.value = false
            return
        }

        coroutineScope.launch {
            Log.d("AddressButton", "walletId: $walletId")

            if(walletId.isEmpty()) {
                Toast.makeText(context, "Error saving address", Toast.LENGTH_SHORT).show()
                isLoading.value = false
                return@launch
            }
            val success = Verification().setResidentialAddress(residentialAddress, walletId)
            if(success) {
                navController.navigate(FaceVerification(
                    userImageUri = ""
                ))
            } else {
                delay(2000)
                isLoading.value = false
            }
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
                        navController.navigate(FaceVerification(
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
                        navController.navigate(homeObject)
                    },
                    enabled = !isLoading.value,
                    modifier = Modifier.padding(vertical = 10.dp)
                )
            }
        }

    }
}
