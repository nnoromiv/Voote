package com.example.voote.navigation

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.voote.view.election.DynamicElectionScreen
import com.example.voote.view.election.ElectionsScreen
import com.example.voote.view.history.HistoryScreen
import com.example.voote.view.home.HomeScreen
import com.example.voote.view.kyc.address.AddressScreen
import com.example.voote.view.kyc.driverlicence.DriverLicenceScreen
import com.example.voote.view.kyc.faceverification.FaceVerificationScreen
import com.example.voote.view.kyc.PersonalVerificationScreen
import com.example.voote.view.kyc.passport.PassportScreen
import com.example.voote.view.login.LoginScreen
import com.example.voote.view.main.MainScreen
import com.example.voote.view.profile.ProfileScreen
import com.example.voote.view.scan.ScanFace
import com.example.voote.view.scan.ScanID
import com.example.voote.view.scan.ScanQR
import com.example.voote.view.signup.SignUpScreen
import com.example.voote.view.signup.TokenVerificationScreen
import com.example.voote.viewModel.AuthViewModel
import com.example.voote.viewModel.KycViewModel
import com.example.voote.viewModel.UserViewModel
import com.example.voote.viewModel.factory.UserViewModelFactory

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current.applicationContext
    val authManager: AuthViewModel = remember { AuthViewModel() }
    val kycViewModel: KycViewModel = remember { KycViewModel() }

    val userViewModel: UserViewModel = viewModel(
        factory = UserViewModelFactory(context as Application)
    )

    val isLoggedIn = remember { authManager.isLoggedIn() }

    // Optionally fetch user data after login
    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            userViewModel.fetchUserData()
            kycViewModel.fetchKycData()
        }
    }

//    authManager.signOut()

//    val startDestination = AddressVerification
    val startDestination = if (isLoggedIn) homeObject else Main


    NavHost(navController = navController, startDestination) {
        composable<Main> {
            MainScreen(navController)
        }
        composable<Login> {
            LoginScreen(navController)
        }
        composable<Signup> {
            SignUpScreen(navController)
        }
        composable<TokenVerification> {
            TokenVerificationScreen(navController)
        }
        composable<PersonalVerification> {
            PersonalVerificationScreen(navController, authManager, userViewModel)
        }
        composable<PassportVerification> {
            PassportScreen(navController, kycViewModel)
        }
        composable<DriverLicenceVerification> {
            DriverLicenceScreen(navController, kycViewModel)
        }
        composable<AddressVerification>{
            AddressScreen(navController, userViewModel, kycViewModel)
        }
        composable<FaceVerification>{
            val args = it.toRoute<FaceVerification>()
            FaceVerificationScreen(navController, args.userImageUri)
        }
        composable<Home>{
            HomeScreen(navController, userViewModel, kycViewModel)
        }
        composable<Elections>{
            ElectionsScreen(navController, kycViewModel)
        }
        composable<Scan>{
            ScanQR()
        }
        composable<History> {
            HistoryScreen(navController)
        }
        composable<Profile> {
            ProfileScreen(navController, userViewModel)
        }
        composable<DynamicElectionScreen> {
            DynamicElectionScreen()
        }

        composable<ScanID> {
            val args = it.toRoute<ScanID>()
            ScanID(navController, args.documentType)
        }

        composable<ScanFace> {
            ScanFace(navController)
        }

    }
}
