package com.example.voote.view.login

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.voote.R
import com.example.voote.firebase.auth.Auth
import com.example.voote.firebase.data.Status
import com.example.voote.firebase.user.User
import com.example.voote.navigation.RouteHome
import com.example.voote.navigation.RoutePersonalVerification
import com.example.voote.navigation.RouteSignup
import com.example.voote.navigation.RouteTokenVerification
import com.example.voote.ui.components.CTextButton
import com.example.voote.ui.components.Loader
import com.example.voote.ui.components.PrimaryButton
import com.example.voote.ui.components.Text
import com.example.voote.viewModel.KycViewModel
import com.example.voote.viewModel.LoginViewModel
import com.example.voote.viewModel.UserViewModel
import kotlinx.coroutines.launch

@Composable
fun LoginButtons(userViewModel: UserViewModel, kycViewModel: KycViewModel, navController: NavController) {

    val auth = Auth()
    val loginViewModel: LoginViewModel = viewModel()

    val email by loginViewModel.email.collectAsState()
    val password by loginViewModel.password.collectAsState()
    val isEmailError by loginViewModel.isEmailError.collectAsState()
    val emailErrorMessage by loginViewModel.emailErrorMessage.collectAsState()

    val errorExist = password.isEmpty() || email.isEmpty() || isEmailError || emailErrorMessage.isNotEmpty()
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current
//    val coroutineScope = (context.applicationContext as ThisApplication).appScope
    val coroutineScope = rememberCoroutineScope()

    fun login() {

        isLoading = true

        coroutineScope.launch {
            val result = auth.logIn(email, password)

            if(result.status == Status.ERROR) {
                Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                isLoading = false
                return@launch
            }

            val uid = result.data?.uid

            if(uid == null) {
                Toast.makeText(context, "Error logging in", Toast.LENGTH_SHORT).show()
                isLoading = false
                return@launch
            }

            val getData = User(uid)

            val user = getData.getUser()
            val kyc = getData.getKycData()

            if(user == null) {
                Toast.makeText(context, "Error getting user", Toast.LENGTH_SHORT).show()
                isLoading = false
                return@launch
            }

            userViewModel.setUserData(user)
            kycViewModel.setKycData(kyc)
            Auth()

            val isPhoneVerified = user.isPhoneVerified
            val phoneNumber = user.phoneNumber
            val walletId = user.walletId


            if(!isPhoneVerified) {
                navController.navigate(RouteTokenVerification(
                    phoneNumber = phoneNumber
                ))
                return@launch
            }

            if(walletId.isEmpty() || kyc == null) {
                navController.navigate(RoutePersonalVerification)
                return@launch
            }

            isLoading = false
            navController.navigate(RouteHome)

            Log.d("Login", "User: $user, Kyc: $kyc")

        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if(isLoading) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ){
                Loader()
            }
        } else {
            PrimaryButton(
                text = stringResource(R.string.login),
                onClick = { login() },
                enabled = !errorExist,
                modifier = Modifier.padding(vertical = 20.dp)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "New Here?",
                fontSize = 18,
                fontWeight = FontWeight.Normal,
            )
            CTextButton(
                text = "Sign Up",
                enabled = !isLoading,
                onClick = {
                    navController.navigate(RouteSignup)
                }
            )
        }
    }
}