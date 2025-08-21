package com.example.voote.view.election

import android.Manifest
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.voote.ThisApplication
import com.example.voote.firebase.data.BreakdownResult
import com.example.voote.firebase.data.WinnerResult
import com.example.voote.firebase.election.Election
import com.example.voote.model.data.CandidateData
import com.example.voote.model.data.ElectionData
import com.example.voote.ui.components.CountDown
import com.example.voote.ui.components.ElectionCandidateColumn
import com.example.voote.ui.components.Loader
import com.example.voote.ui.components.Text
import com.example.voote.viewModel.AuthViewModel
import com.example.voote.viewModel.WalletViewModel
import kotlinx.coroutines.launch


@RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
@Composable
fun DynamicElectionScreen(id: String, authManager: AuthViewModel, walletViewModel: WalletViewModel) {

    val context = LocalContext.current

    val election = Election()
    val electionData  = remember { mutableStateOf<ElectionData?>(null) }
    val candidatesList = remember { mutableStateListOf<CandidateData>() }
    var userAddress by remember { mutableStateOf<String?>("") }
    val breakDownResult = remember { mutableStateOf(BreakdownResult()) }
    val winnerResult = remember { mutableStateOf(WinnerResult()) }

    val contract = authManager.contract.collectAsState().value
    val walletData by walletViewModel.walletData.collectAsState()
    val coroutineScope = (context.applicationContext as ThisApplication).appScope


    suspend fun loadAddressAndElectionData() {
        userAddress = walletData?.address.toString()
        val result = election.getElectionData(id)

        if(result == null) return

        electionData.value = result

        val candidates = election.getCandidatesForElection(result.id)
        candidatesList.addAll(candidates)
    }

    LaunchedEffect(true) {
        loadAddressAndElectionData()
    }

    fun callBlockchainElectionData() {
        val electionId = electionData.value?.electionId?.toBigInteger()
        if(electionId == null) return

        coroutineScope.launch {
            val resultBreakDown = contract?.getResultBreakdown(electionId)?.send()
            val resultWinner = contract?.getWinner(electionId)?.send()

            if(resultBreakDown != null) {
                breakDownResult.value = BreakdownResult(
                    candidatesName = resultBreakDown.component1(),
                    startTime = resultBreakDown.component2(),
                    endTime = resultBreakDown.component3(),
                    voteCounts = resultBreakDown.component4(),
                    percentages = resultBreakDown.component5()
                )
            }

            if(resultWinner != null) {
                winnerResult.value = WinnerResult(
                    candidatesName = resultWinner.component1(),
                    candidatesAddress = resultWinner.component2(),
                    voteCount = resultWinner.component3()
                )
            }
        }
    }

    LaunchedEffect(electionData.value) {
        callBlockchainElectionData()
    }


    Scaffold {
        innerPadding ->
        Column(
            modifier = Modifier.fillMaxWidth().padding(innerPadding).padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val data = electionData.value

            if(data == null || candidatesList.isEmpty()) {
                Loader()
            } else {
                Text(
                    text = data.title,
                    fontSize = 16,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.weight(1f)
                    ){
                        CountCard(
                            label = "Registered Voters",
                            value = data.registeredAddresses.count().toString()
                        )
                    }
                    Box(
                        modifier = Modifier.weight(1f)
                    ){
                        CountCard(
                            label = "Total Votes",
                            value = breakDownResult.value.voteCounts?.sumOf { it?.toInt() ?: 0 }.toString()
                        )
                    }
                }

                CountCard(
                    label = "Winner Vote Count",
                    value = winnerResult.value.voteCount.toString()
                )

                CountDown(
                    text = data.title,
                    endTime = data.endTime
                )

                ElectionCandidateColumn(
                    election = data,
                    walletViewModel = walletViewModel,
                    contract = contract!!,
                    userAddress,
                    candidates = candidatesList,
                    isDynamic = true
                )
            }
        }
    }
}


