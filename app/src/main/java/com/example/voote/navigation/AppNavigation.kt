package com.example.voote.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.voote.view.election.DynamicElectionScreen
import com.example.voote.view.election.ElectionsScreen
import com.example.voote.view.history.HistoryScreen
import com.example.voote.view.home.HomeScreen
import com.example.voote.view.kyc.AddressScreen
import com.example.voote.view.kyc.DriverLicenceScreen
import com.example.voote.view.kyc.FaceVerificationScreen
import com.example.voote.view.kyc.IdentityNumberScreen
import com.example.voote.view.kyc.PersonalVerificationScreen
import com.example.voote.view.login.LoginScreen
import com.example.voote.view.main.MainScreen
import com.example.voote.view.profile.ProfileScreen
import com.example.voote.view.scan.ScanScreen
import com.example.voote.view.signup.SignUpScreen
import com.example.voote.view.signup.TokenVerificationScreen


@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.MAIN) {
            MainScreen(navController)
        }
        composable(Routes.LOGIN) {
            LoginScreen(navController)
        }
        composable(Routes.SIGNUP) {
            SignUpScreen(navController)
        }
        composable(Routes.TOKEN_VERIFICATION) {
            TokenVerificationScreen(navController)
        }
        composable(Routes.PERSONAL_VERIFICATION) {
            PersonalVerificationScreen(navController)
        }
        composable(Routes.IDENTITY_NUMBER) {
            IdentityNumberScreen(navController)
        }
        composable(Routes.DRIVER_LICENCE) {
            DriverLicenceScreen(navController)
        }
        composable(Routes.ADDRESS) {
            AddressScreen(navController)
        }
        composable(Routes.FACE_VERIFICATION) {
            FaceVerificationScreen(navController)
        }
        composable(Routes.HOME) {
            HomeScreen(navController)
        }
        composable(Routes.ELECTIONS) {
            ElectionsScreen(navController)
        }
        composable(Routes.SCAN) {
            ScanScreen()
        }
        composable(Routes.HISTORY) {
            HistoryScreen(navController)
        }
        composable(Routes.PROFILE) {
            ProfileScreen(navController)
        }
        composable(Routes.DYNAMIC_ELECTION_SCREEN) {
            DynamicElectionScreen()
        }

    }
}