package com.example.voote.ui.components

import android.Manifest
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.voote.model.data.CandidateData
import com.example.voote.model.data.ElectionData
import com.example.voote.navigation.RouteDynamicElection
import com.example.voote.viewModel.AuthViewModel
import com.example.voote.viewModel.WalletViewModel

@RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
@Composable
fun ElectionsWithCandidatesSection(elections: List<ElectionData>, candidatesMap: Map<String, List<CandidateData>>, authManager: AuthViewModel, walletViewModel: WalletViewModel, navController: NavController) {

    var userAddress by remember { mutableStateOf<String?>("") }

    val uid = authManager.userUid().toString()
    val contract = authManager.contract.collectAsState().value
    val walletData by walletViewModel.walletData.collectAsState()

    LaunchedEffect(Unit) {
        userAddress = walletData?.address.toString()
    }

    Column(modifier = Modifier.padding(bottom = 10.dp)) {
        elections.forEach { election ->
            val candidates = candidatesMap[election.id] ?: emptyList()

            Column(modifier = Modifier.padding(bottom = 10.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = election.title,
                        fontSize = 16,
                        fontWeight = FontWeight.Bold,
                    )

                    if(candidates.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                navController.navigate(RouteDynamicElection(
                                    id = election.id
                                ))
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                                contentDescription = "See All"
                            )
                        }
                    }
                }

                // Show candidates for this election
                ElectionCandidateColumn(
                    election,
                    uid,
                    walletViewModel,
                    contract = contract!!,
                    userAddress = userAddress,
                    candidates = candidates
                )
            }
        }
    }
}
