package com.example.voote.view.home

import android.Manifest
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.QrCode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.composables.icons.lucide.LayoutDashboard
import com.composables.icons.lucide.ListPlus
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Vote
import com.example.voote.contract.Voote
import com.example.voote.ui.components.BottomNavigation
import com.example.voote.ui.components.CountDown
import com.example.voote.ui.components.ElectionsWithCandidatesSection
import com.example.voote.ui.components.KYCReminder
import com.example.voote.ui.components.ProfileBar
import com.example.voote.ui.components.Text
import com.example.voote.view.LoaderScreen
import com.example.voote.view.modals.CreateElectionModal
import com.example.voote.view.modals.RegisterAddressToVote
import com.example.voote.view.modals.RegisterCandidate
import com.example.voote.view.modals.UserAddressQR
import com.example.voote.viewModel.AuthViewModel
import com.example.voote.viewModel.HomeViewModel
import com.example.voote.viewModel.KycViewModel
import com.example.voote.viewModel.WalletViewModel
import kotlinx.coroutines.launch

@RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel : HomeViewModel = viewModel(), kycViewModel: KycViewModel, authManager: AuthViewModel, walletViewModel: WalletViewModel, navController: NavController) {

    val isLoading by viewModel.isLoading.collectAsState()

    val isLoggedIn by remember { derivedStateOf{ authManager.isLoggedIn() } }
    val kycCompletionPercentage by kycViewModel.kycCompletionPercentage.collectAsState()

    val userAddress = viewModel.userAddress
    val name = viewModel.name
    val userName = viewModel.userName

    val contract = authManager.contract.collectAsState().value

    val allElectionData  by viewModel.allElectionData.collectAsState()
    val candidatesMap by viewModel.candidatesMap.collectAsState()
    val cardData by viewModel.cardData.collectAsState()

    val userAddressQRSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()
    var userAddressShowState by remember { mutableStateOf(false) }

    var isRefreshing by remember { mutableStateOf(false) }


    fun handleUserAddressQR() {
        userAddressShowState = true
        coroutineScope.launch {
            userAddressQRSheetState.show()
        }
    }

    LaunchedEffect(isLoggedIn) {
        Log.d("HomeScreen", "isLoggedIn: $isLoggedIn")
        viewModel.loadData(isLoggedIn)
    }

    val isBlockChainRequirementsLoaded = userAddress != null && contract != null && !isLoading

    if(!isBlockChainRequirementsLoaded) {
        LoaderScreen()
    } else {
        Scaffold(
            bottomBar = {
                BottomNavigation(navController)
            }
        ) { innerPadding ->
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = {
                    coroutineScope.launch {
                        isRefreshing = true
                        viewModel.loadData(isLoggedIn)
                        isRefreshing = false
                    }
                }
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(10.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    ProfileBar(
                        name.toString(),
                        userName.toString(),
                        imageVector = Icons.Outlined.QrCode,
                        onClick = { handleUserAddressQR() }
                    )
                    KYCReminder(
                        kycCompletionPercentage,
                        navController,
                    )

                    cardData?.let {
                        CountDown(
                            text = it.title,
                            endTime = it.endTime,
                            contractAddress = userAddress
                        )
                    }

                    ElectionActionButtons(contract, userAddress, authManager, walletViewModel)

                    Spacer(modifier = Modifier.size(10.dp))

                    if(allElectionData.isEmpty()) {
                        Row(
                            modifier = Modifier.size(300.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "No Current Election",
                                fontWeight = FontWeight.Bold,
                                fontSize = 20
                            )
                        }
                    } else {
                        ElectionsWithCandidatesSection(allElectionData, candidatesMap, authManager, walletViewModel, navController)
                    }

                    if (userAddressShowState) {
                        UserAddressQR(
                            userAddress,
                            sheetState = userAddressQRSheetState,
                            onDismissRequest = {
                                userAddressShowState = false
                            }
                        )
                    }
                }
            }
        }
    }


}


@RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ElectionActionButtons(contract: Voote, userAddress: String?, authManager: AuthViewModel, walletViewModel: WalletViewModel) {
    val coroutineScope = rememberCoroutineScope()

    val createElectionSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var createElectionShowSheet by remember { mutableStateOf(false) }

    val registerCandidateSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var registerCandidateShowSheet by remember { mutableStateOf(false) }

    val registerAddressSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var registerAddressShowSheet by remember { mutableStateOf(false) }

    fun handleRegisterAddress() {
        registerAddressShowSheet = true
        registerCandidateShowSheet = false
        createElectionShowSheet = false
        coroutineScope.launch {
            registerAddressSheetState.show()
        }
    }

    fun handleCreateElectionModal() {
        registerAddressShowSheet = false
        registerCandidateShowSheet = false
        createElectionShowSheet = true
        coroutineScope.launch {
            createElectionSheetState.show()
        }
    }

    fun handleRegisterCandidateModal() {
        registerAddressShowSheet = false
        registerCandidateShowSheet = true
        createElectionShowSheet = false
        coroutineScope.launch {
            registerCandidateSheetState.show()
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            IconButton(
                onClick = { handleRegisterAddress() }
            ) {
                Icon(
                    imageVector = Lucide.Vote,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }
            Text(
                text = "Register",
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            IconButton(
                onClick = { handleCreateElectionModal() }
            ) {
                Icon(
                    imageVector = Lucide.ListPlus,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }
            Text(
                text = "Create",
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            IconButton(
                onClick = { handleRegisterCandidateModal() }
            ) {
                Icon(
                    imageVector = Lucide.LayoutDashboard,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }
            Text(
                text = "Candidate",
            )
        }

    }

    if (registerAddressShowSheet) {
        RegisterAddressToVote(
            authManager,
            sheetState = registerAddressSheetState,
            onDismissRequest = {
                coroutineScope.launch {
                    registerAddressSheetState.hide()
                    registerAddressShowSheet = false
                }
            }
        )
    }

    if (createElectionShowSheet) {
        CreateElectionModal(
            contract,
            userAddress,
            authManager,
            walletViewModel,
            sheetState = createElectionSheetState,
            onDismissRequest = {
                coroutineScope.launch {
                    createElectionSheetState.hide()
                    createElectionShowSheet = false
                }
            }
        )
    }

    if (registerCandidateShowSheet) {
        RegisterCandidate(
            contract,
            userAddress,
            authManager,
            walletViewModel,
            sheetState = registerCandidateSheetState,
            onDismissRequest = {
                coroutineScope.launch {
                    registerCandidateSheetState.hide()
                    registerCandidateShowSheet = false
                }
            }
        )
    }
}

