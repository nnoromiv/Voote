package com.example.voote.view.election

import android.Manifest
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.voote.firebase.election.Election
import com.example.voote.model.data.CandidateData
import com.example.voote.model.data.ElectionData
import com.example.voote.ui.components.BottomNavigation
import com.example.voote.ui.components.ElectionsWithCandidatesSection
import com.example.voote.ui.components.KYCReminder
import com.example.voote.ui.components.Loader
import com.example.voote.viewModel.AuthViewModel
import com.example.voote.viewModel.KycViewModel
import com.example.voote.viewModel.WalletViewModel

@RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
@Composable
fun ElectionsScreen(authManager: AuthViewModel, walletViewModel: WalletViewModel, kycViewModel: KycViewModel, navController: NavController) {

    val isLoggedIn by remember { derivedStateOf{ authManager.isLoggedIn() } }
    val isLoading = remember { mutableStateOf(false) }

    val election = Election()
    val allElectionData = remember { mutableStateListOf<ElectionData>() }
    val candidatesMap = remember { mutableStateMapOf<String, List<CandidateData>>() }

    val kycCompletionPercentage by kycViewModel.kycCompletionPercentage.collectAsState()

    suspend fun loadData() {
        if (!isLoggedIn) return
        isLoading.value = true

        val result = election.getAllElectionsSortedByTitle()

        allElectionData.clear()
        candidatesMap.clear()
        if (result.isNotEmpty()) {
            allElectionData.addAll(result)
            for (elections in result) {
                val candidates = election.getCandidatesForElection(elections.id)
                candidatesMap[elections.id] = candidates
            }
        }

        isLoading.value = false
    }

    LaunchedEffect(Unit) {
        loadData()
    }

    Scaffold (
        bottomBar = { BottomNavigation(navController) }
    ) {
            innerPadding ->
        Column (
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(10.dp)
                .verticalScroll(rememberScrollState())
        ) {

            if (isLoading.value) {
                Loader()
            }

            KYCReminder(
                kycCompletionPercentage,
                navController
            )

            Spacer(modifier = Modifier.size(10.dp))

            ElectionsWithCandidatesSection(allElectionData, candidatesMap, authManager, walletViewModel,  navController)

        }
    }
}
