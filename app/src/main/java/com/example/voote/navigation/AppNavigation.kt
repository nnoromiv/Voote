package com.example.voote.navigation

import android.Manifest
import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.voote.blockchain.Connector
import com.example.voote.firebase.auth.Auth
import com.example.voote.firebase.user.User
import com.example.voote.view.ImportWalletScreen
import com.example.voote.view.LoaderScreen
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
import com.example.voote.view.report.StatusScreen
import com.example.voote.view.scan.ScanFace
import com.example.voote.view.scan.ScanID
import com.example.voote.view.scan.ScanQR
import com.example.voote.view.signup.SignUpScreen
import com.example.voote.view.signup.TokenVerificationScreen
import com.example.voote.viewModel.AuthViewModel
import com.example.voote.viewModel.IdentityDetailsViewModel
import com.example.voote.viewModel.KycViewModel
import com.example.voote.viewModel.UserViewModel
import com.example.voote.viewModel.WalletViewModel
import com.example.voote.viewModel.factory.UserViewModelFactory
import com.example.voote.wallet.WalletManager
import java.io.File

@RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val authManager: AuthViewModel = remember { AuthViewModel() }
    val kycViewModel: KycViewModel = remember { KycViewModel() }
    val walletViewModel: WalletViewModel = remember { WalletViewModel() }
    val userViewModel: UserViewModel = viewModel(
        factory = UserViewModelFactory(context.applicationContext as Application)
    )
    val identityDetailsViewModel: IdentityDetailsViewModel = remember { IdentityDetailsViewModel() }

    val connector = Connector(walletViewModel)
    val isLoggedIn by remember { derivedStateOf{ authManager.isLoggedIn() } }

    suspend fun loadApplicationWalletAndSetContract() {
        try {
            val walletContext = WalletManager(context)
            val walletFile = File(context.filesDir, "wallets").listFiles()?.firstOrNull()

            walletFile?.name?.let { fileName ->
                walletContext.getWalletContent(fileName)?.let { storedWallet ->
                    walletViewModel.setWalletData(storedWallet)

                    val contract = connector.connectContract(storedWallet.privateKey)

                    if (contract == null) {
                        Log.e("Connector", "Failed to connect: Contract is null.")
                    }

//                    split entry hint excite remain whisper wink vault body have arrange engage
//                    0xf1b882aaf4c9e0d08d0c6ab7387c3cee706f3b84
//                    a6b19d9ebe577e781ef8efedd92d68f86021c6c9e47008825660d6efc5b780d4

                    authManager.setContract(contract)
                }
            }
        } catch (e: Exception) {
            Log.e("WalletCheck", "Error accessing wallet file", e)
        }
    }

    suspend fun loadAndNavigateUserOnLogin() {
        if (!isLoggedIn) {
            return
        }

        val uid = authManager.userUid()

        if(uid == null) {
            Toast.makeText(context, "Error logging in", Toast.LENGTH_SHORT).show()
            return
        }

        val getData = User(uid)

        val user = getData.getUser()
        val kyc = getData.getKycData()

        if(user == null) {
            Toast.makeText(context, "Error getting user", Toast.LENGTH_SHORT).show()
            return
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
            return
        }

        if(walletId.isEmpty() || kyc == null) {
            navController.navigate(RoutePersonalVerification)
            return
        }

        navController.navigate(RouteHome)
    }

    LaunchedEffect(isLoggedIn) {
        loadApplicationWalletAndSetContract()
        loadAndNavigateUserOnLogin()
    }

    val startDestination: AppRoute = when {
        !isLoggedIn -> RouteMain
        else -> RouteHome
    }

    NavHost(navController = navController, startDestination) {
        composable<RouteMain> {
            MainScreen(authManager, walletViewModel, navController)
        }

        composable<RouteLogin> {
            LoginScreen(authManager, userViewModel, kycViewModel, navController)
        }

        composable<RouteSignup> {
            SignUpScreen(userViewModel, kycViewModel, navController)
        }

        composable<RouteTokenVerification> {
            val args = it.toRoute<RouteTokenVerification>()
            TokenVerificationScreen(args.phoneNumber, navController)
        }

        composable<RoutePersonalVerification> {
            PersonalVerificationScreen(authManager, userViewModel, walletViewModel, navController)
        }

        composable<RoutePassportVerification> {
            PassportScreen(authManager, kycViewModel, navController)
        }

        composable<RouteDriverLicenceVerification> {
            DriverLicenceScreen(authManager, kycViewModel, navController)
        }

        composable<RouteAddressVerification>{
            AddressScreen(authManager, userViewModel, kycViewModel, navController)
        }

        composable<RouteFaceVerification>{
            val args = it.toRoute<RouteFaceVerification>()
            FaceVerificationScreen(authManager, args.userImageUri, navController)
        }

        composable<RouteHome>{
            HomeScreen(userViewModel, kycViewModel, authManager, walletViewModel, navController)
        }

        composable<RouteElections>{
            ElectionsScreen(authManager, walletViewModel, kycViewModel, navController)
        }

        composable<RouteScan>{
            ScanQR()
        }

        composable<RouteHistory> {
            HistoryScreen(authManager, walletViewModel, navController)
        }

        composable<RouteProfile> {
            ProfileScreen(authManager, kycViewModel, userViewModel, walletViewModel, navController)
        }

        composable<RouteDynamicElection> {
            val args = it.toRoute<RouteDynamicElection>()
            DynamicElectionScreen(args.id, authManager, walletViewModel)
        }

        composable<RouteScanID> {
            val args = it.toRoute<RouteScanID>()
            ScanID(authManager, args.documentType, identityDetailsViewModel, userViewModel, navController)
        }

        composable<RouteScanFace> {
            ScanFace(navController)
        }

        composable<RouteStatus>{
            val args = it.toRoute<RouteStatus>()
            StatusScreen(args.status, args.nextScreen, navController)
        }

        composable<RouteLoader> {
            LoaderScreen()
        }

        composable<RouteImportWallet> {
            ImportWalletScreen(walletViewModel, navController)
        }

    }
}
