package com.example.voote.navigation

import androidx.compose.runtime.Composable
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
import com.example.voote.view.kyc.identitynumber.IdentityNumberScreen
import com.example.voote.view.kyc.PersonalVerificationScreen
import com.example.voote.view.login.LoginScreen
import com.example.voote.view.main.MainScreen
import com.example.voote.view.profile.ProfileScreen
import com.example.voote.view.scan.ScanFace
import com.example.voote.view.scan.ScanID
import com.example.voote.view.scan.ScanQR
import com.example.voote.view.signup.SignUpScreen
import com.example.voote.view.signup.TokenVerificationScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Main) {
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
            PersonalVerificationScreen(navController)
        }
        composable<IdentityNumber> {
            IdentityNumberScreen(navController)
        }
        composable<DriverLicence> {
            DriverLicenceScreen(navController)
        }
        composable<Address>{
            AddressScreen(navController)
        }
        composable<FaceVerification>{
            val args = it.toRoute<FaceVerification>()
            FaceVerificationScreen(navController, args.userImageUri)
        }
        composable<Home>{
            HomeScreen(navController)
        }
        composable<Elections>{
            ElectionsScreen(navController)
        }
        composable<Scan>{
            ScanQR()
        }
        composable<History> {
            HistoryScreen(navController)
        }
        composable<Profile> {
            ProfileScreen(navController)
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
