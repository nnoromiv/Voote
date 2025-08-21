package com.example.voote.ui.components

import android.Manifest
import android.annotation.SuppressLint
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.NoAccounts
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.voote.contract.Voote
import com.example.voote.model.data.CandidateData
import com.example.voote.model.data.ElectionData
import com.example.voote.viewModel.WalletViewModel
import kotlinx.coroutines.launch

@RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ElectionCandidateColumn(election : ElectionData?, walletViewModel: WalletViewModel, contract: Voote, userAddress: String?, candidates: List<CandidateData>, isDynamic: Boolean = false) {

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()
    var showSheet by remember { mutableStateOf(false) }
    var selectedCandidate by remember { mutableStateOf<CandidateData?>(null) }

    if(candidates.isEmpty()) {
        Column (
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Outlined.NoAccounts,
                contentDescription = "No Candidates Found",
                modifier = Modifier.padding(bottom = 10.dp)
            )
            Text(
                text = "No Candidates Found",
                fontSize = 14,
                fontWeight = FontWeight.Bold
            )
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (isDynamic) 1000.dp else 150.dp)
        ) {

            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier.wrapContentHeight().padding(bottom = 5.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(candidates.size) { index ->
                    CandidateCard(
                        candidateImageUri = candidates[index].candidateImageUrl,
                        candidateName = candidates[index].candidateName,
                        onClick = {
                            selectedCandidate = candidates[index]
                            showSheet = true
                            coroutineScope.launch {
                                sheetState.show()
                            }
                        }
                    )
                }
            }
        }
    }

    if (showSheet && selectedCandidate != null) {
        CandidateModal(
            election = election,
            walletViewModel = walletViewModel,
            contract = contract,
            userAddress = userAddress,
            selectedCandidate = selectedCandidate,
            sheetState = sheetState,
            onDismissRequest = {
                showSheet = false
                selectedCandidate = null
            }
        )
    }
}
