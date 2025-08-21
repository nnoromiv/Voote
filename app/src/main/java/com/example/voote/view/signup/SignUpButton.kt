package com.example.voote.view.signup

import android.app.Activity
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.voote.R
import com.example.voote.firebase.auth.Auth
import com.example.voote.firebase.data.Status
import com.example.voote.firebase.user.User
import com.example.voote.navigation.RouteLogin
import com.example.voote.navigation.RouteTokenVerification
import com.example.voote.ui.components.CTextButton
import com.example.voote.ui.components.Loader
import com.example.voote.ui.components.PrimaryButton
import com.example.voote.ui.components.Text
import com.example.voote.viewModel.KycViewModel
import com.example.voote.viewModel.SignUpViewModel
import com.example.voote.viewModel.UserViewModel
import kotlinx.coroutines.launch

@Composable
fun SignUpButton(activity: Activity, userViewModel: UserViewModel, kycViewModel: KycViewModel, navController: NavController) {

    val auth = Auth()

    val signUpViewModel : SignUpViewModel = viewModel()
    val email by signUpViewModel.email.collectAsState()
    val firstName by signUpViewModel.firstName.collectAsState()
    val lastName by signUpViewModel.lastName.collectAsState()
    val phoneNumber by signUpViewModel.phoneNumber.collectAsState()
    val password by signUpViewModel.password.collectAsState()
    val isEmailError by signUpViewModel.isEmailError.collectAsState()
    val emailErrorMessage by signUpViewModel.emailErrorMessage.collectAsState()
    val isPhoneNumberError by signUpViewModel.isPhoneNumberError.collectAsState()
    val phoneNumberErrorMessage by signUpViewModel.phoneNumberErrorMessage.collectAsState()

//    val context = LocalContext.current
//    val coroutineScope = (context.applicationContext as ThisApplication).appScope
    val coroutineScope = rememberCoroutineScope()

    val errorExist = firstName.isEmpty() || lastName.isEmpty() || password.isEmpty() || email.isEmpty() || phoneNumber.isEmpty() || isEmailError || isPhoneNumberError || emailErrorMessage.isNotEmpty() || phoneNumberErrorMessage.isNotEmpty()
    var isLoading by remember { mutableStateOf(false) }

    fun handleSignUp() {

        isLoading = true

        if(password.length < 6) {
            Toast.makeText(activity, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
            isLoading = false
            return
        }

        coroutineScope.launch {
            val result = auth.signUp(email, firstName, lastName, phoneNumber, password)

            if(result.status == Status.ERROR) {
                Toast.makeText(activity, result.message, Toast.LENGTH_SHORT).show()
                isLoading = false
                return@launch
            }


            val data = result.data?.user

            if(data == null) {
                Toast.makeText(activity, "Error Signing Up", Toast.LENGTH_SHORT).show()
                isLoading = false
                return@launch

            }

            val uid = data.uid
            val getData = User(uid)

            val user = getData.getUser()
            val kyc = getData.getKycData()

            if(user == null) {
                Log.d("SignUp", "Error getting user")
                isLoading = false
                return@launch
            }

            userViewModel.setUserData(user)
            kycViewModel.setKycData(kyc)
            Auth()

            isLoading = false

            navController.navigate(RouteTokenVerification(
                phoneNumber = phoneNumber,
            ))
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
                text = stringResource(R.string.sign_up),
                onClick = { handleSignUp() },
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
                text = "Already a user?",
                fontSize = 18,
                fontWeight = FontWeight.Normal,
            )
            CTextButton(
                text = "Log In",
                onClick = { navController.navigate(RouteLogin) }
            )
        }
    }
}
