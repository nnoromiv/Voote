package com.example.voote.ui.components

import android.Manifest
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.BadgeCheck
import com.composables.icons.lucide.Earth
import com.composables.icons.lucide.Facebook
import com.composables.icons.lucide.Linkedin
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Twitter
import com.example.voote.ThisApplication
import com.example.voote.blockchain.Connector
import com.example.voote.contract.Voote
import com.example.voote.firebase.data.AUDIT
import com.example.voote.firebase.data.STATUS
import com.example.voote.firebase.data.VotePreview
import com.example.voote.firebase.user.User
import com.example.voote.model.data.CandidateData
import com.example.voote.model.data.ElectionData
import com.example.voote.utils.helpers.openWebsite
import com.example.voote.utils.helpers.sendNotification
import com.example.voote.viewModel.WalletViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
@RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
@OptIn(ExperimentalMaterial3Api::class)
fun CandidateModal(election: ElectionData?, uid: String, walletViewModel: WalletViewModel, contract: Voote, userAddress: String?, selectedCandidate: CandidateData?, sheetState: SheetState, onDismissRequest: () -> Unit) {

    val isLoading = remember { mutableStateOf(false) }
    val connector = Connector(walletViewModel)
    var showPreviewDialog by remember { mutableStateOf(false) }
    val contractAddress = contract.contractAddress.toString()
    val fromAddress = userAddress.toString()
    val gasData = remember { mutableStateOf(VotePreview(null, null, null)) }
    val context = LocalContext.current

    val electionId = election?.electionId ?: 0
    val registeredAddresses = election?.registeredAddresses ?: emptyList()

    val coroutineScope = (context.applicationContext as ThisApplication).appScope

    val errorExist = electionId == 0 || selectedCandidate == null

    fun handleVoteCandidatePreviewOnly() {
        if (errorExist) {
            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        if(!registeredAddresses.contains(userAddress)) {
            Toast.makeText(context, "You are not registered to vote", Toast.LENGTH_SHORT).show()
            return
        }

        coroutineScope.launch {
            try {

                 val previewResult = withContext(Dispatchers.IO) {
                    connector.previewVote(
                        electionId.toBigInteger(),
                        selectedCandidate.candidateId.toBigInteger()
                    )
                }

                if (previewResult.errorMessage != null) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, previewResult.errorMessage, Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }

                gasData.value = previewResult

                showPreviewDialog = true

            } catch (e: Exception) {
                Log.e("SmartContractPreview", "Failed to preview transaction", e)
                Toast.makeText(context, "Failed to preview transaction", Toast.LENGTH_SHORT).show()
            }
        }
    }

    suspend fun handleVoteCandidate() {
        val user = User(uid)

        if(errorExist) {
            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        isLoading.value = true

        try {
            showPreviewDialog = false

            val receipt = withContext(Dispatchers.IO) {
                contract.vote(electionId.toBigInteger(), selectedCandidate.candidateId.toBigInteger()).send()
            }

            if(!receipt.isStatusOK) {
                user.writeAuditLog(AUDIT.VOTE, STATUS.ERROR, receipt.transactionHash.toString())
                sendNotification(context, "Error Voting", "Transaction failed")
                isLoading.value = false
                Log.d("SmartContract", "Vote error: $receipt")
                return
            }

            sendNotification(context, "Vote cast successfully", "Transaction Success")
            user.writeAuditLog(AUDIT.VOTE, STATUS.SUCCESS, receipt.transactionHash.toString())
            isLoading.value = false
            onDismissRequest()
            Log.d("SmartContract", "Vote cast: $receipt")
        } catch (e: Exception) {
            Log.e("SmartContract", "Failed to vote", e)

            if(e.message?.contains("insufficient funds") == true) {
                sendNotification(context, "Insufficient funds", "Add Some tokens")
                return
            }
            isLoading.value = false
            sendNotification(context, "Failed to vote", "Retry Later")
        }

    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = Color.White,
    ) {
        Column(
            modifier = Modifier
                .padding(start = 10.dp, bottom = 10.dp, end = 10.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            ProfileBar(
                name = selectedCandidate!!.candidateName,
                userName = selectedCandidate.candidateName.take(5),
                imageVector = Lucide.BadgeCheck,
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    PrimaryButton(
                        text = "Biography",
                        onClick = { openWebsite(context, selectedCandidate.candidateBiographyLink) },
                        colors = ButtonColors(
                            containerColor = Color(0xFF6B6B6B),
                            contentColor = Color.White,
                            disabledContainerColor = Color(0xFF1B1B1B),
                            disabledContentColor = Color.White
                        )
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    COutlinedButton(
                        text = "Campaign",
                        onClick = { openWebsite(context, selectedCandidate.candidateCampaignLink) }
                    )
                }
            }

            Socials()

            PrimaryButton(
                text = "Vote ${selectedCandidate.candidateName}",
                onClick = { handleVoteCandidatePreviewOnly() },
            )
        }

    }

    if(showPreviewDialog) {
        TransactionPreviewDialog(
            showDialog = true,
            onDismiss = { showPreviewDialog = false },
            onConfirm = {
                coroutineScope.launch {
                    try {
                        handleVoteCandidate()
                    } catch (e: Exception) {
                        Log.e("CreateElectionModal", "Error during election creation", e)
                    }
                }
            },
            fromAddress = fromAddress,
            contractAddress = contractAddress,
            gasData = gasData.value,
        )
    }
}

@Composable
fun Socials() {
    Box(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        contentAlignment = Alignment.CenterEnd
    ) {
        Row (
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = Lucide.Facebook,
                contentDescription = "Facebook",
                modifier = Modifier.size(24.dp)
            )
            Icon(
                imageVector = Lucide.Twitter,
                contentDescription = "X f.k.a Twitter",
                modifier = Modifier.size(24.dp)
            )
            Icon(
                imageVector = Lucide.Linkedin,
                contentDescription = "LinkedIn",
                modifier = Modifier.size(24.dp)
            )
            Icon(
                imageVector = Lucide.Earth,
                contentDescription = "Website",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
