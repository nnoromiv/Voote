package com.example.voote.view.election

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.voote.ui.components.BottomNavigation
import com.example.voote.ui.components.ElectionBlock
import com.example.voote.ui.components.KYCReminder
import com.example.voote.view.home.SearchForElection
import com.example.voote.viewModel.KycViewModel

@Composable
fun ElectionsScreen(navController: NavController, kycViewModel: KycViewModel
) {
    var searchValue by remember { mutableStateOf("") }

    val kycCompletionPercentage by kycViewModel.kycCompletionPercentage.collectAsState()

    Scaffold (
        bottomBar = { BottomNavigation(navController) }
    ) {
        innerPadding ->
        Column (
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(10.dp)
                .verticalScroll(rememberScrollState())
        ) {

            KYCReminder(
                navController,
                kycCompletionPercentage
            )

            SearchForElection(
                searchValue = searchValue,
                onSearchValueChange = { searchValue = it }
            )

            ElectionBlock(navController)

            ElectionBlock(navController)

            ElectionBlock(navController)
        }
    }
}