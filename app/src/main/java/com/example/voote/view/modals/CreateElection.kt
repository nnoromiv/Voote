package com.example.voote.view.modals

import android.Manifest
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.voote.ui.components.DateInputField
import com.example.voote.ui.components.PrimaryButton
import com.example.voote.ui.components.Text
import com.example.voote.ui.components.TextField
import com.example.voote.viewModel.ElectionViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.example.voote.blockchain.Connector
import com.example.voote.contract.Voote
import com.example.voote.firebase.data.AUDIT
import com.example.voote.firebase.data.STATUS
import com.example.voote.firebase.data.VotePreview
import com.example.voote.firebase.user.User
import com.example.voote.ui.components.Loader
import com.example.voote.ui.components.TransactionPreviewDialog
import com.example.voote.utils.helpers.convertLongToDate
import com.example.voote.viewModel.AuthViewModel
import com.example.voote.viewModel.WalletViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigInteger
import java.time.LocalDate
import java.time.ZoneId
import com.example.voote.utils.helpers.sendNotification

@RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateElectionModal(contract: Voote, userAddress: String?, authManager: AuthViewModel, walletViewModel: WalletViewModel, sheetState: SheetState, onDismissRequest: () -> Unit) {
    val isLoading = remember { mutableStateOf(false) }
    val electionViewModel: ElectionViewModel = viewModel()
    val electionTitle by electionViewModel.electionTitle.collectAsState()
    val startTime by electionViewModel.startTime.collectAsState()
    val endTime by electionViewModel.endTime.collectAsState()
    val connector = Connector(walletViewModel)
    var showPreviewDialog by remember { mutableStateOf(false) }
    val contractAddress = contract.contractAddress.toString()
    val uid = authManager.userUid().toString()
    val fromAddress = userAddress.toString()
    val gasData = remember { mutableStateOf(VotePreview(null, null, null)) }
    val user = User(uid)
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    fun onElectionStartTimeChange(value: Long) {
        val todayStartMillis = LocalDate.now()
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        if (value < todayStartMillis + 86_400_000) { // Entire today range
            Toast.makeText(context, "Start date must be after today", Toast.LENGTH_SHORT).show()
            return
        }

        electionViewModel.setStartTime(value)
    }

    fun onElectionEndTimeChange(value: Long) {
        if(startTime == 0L) {
            Toast.makeText(context, "Please select a start date first", Toast.LENGTH_SHORT).show()
            return
        }

        if(value <= startTime) {
            Toast.makeText(context, "End date cannot be before start date", Toast.LENGTH_SHORT).show()
            return
        }

        electionViewModel.setEndTime(value)
    }

    val errorExist = electionTitle.isEmpty() || startTime == 0L || endTime == 0L || endTime <= startTime

    fun handleCreateElectionPreviewOnly() {
        if (errorExist) {
            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        coroutineScope.launch {
            try {

                val previewResult =  withContext(Dispatchers.IO) {
                    connector.previewCreateElection(electionTitle, startTime, endTime)
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

    suspend fun handleCreateElection() {
        if(errorExist) {
            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val start: BigInteger = BigInteger.valueOf(startTime / 1000)
        val end: BigInteger = BigInteger.valueOf(endTime / 1000)

        isLoading.value = true

        try {
            showPreviewDialog = false

            val receipt = withContext(Dispatchers.IO) {
                contract.createElection(electionTitle, start, end).send()
            }

            if(!receipt.isStatusOK) {
                sendNotification(context, "Error Creating Election", "Transaction failed")
                isLoading.value = false
                Log.d("SmartContract", "Election error: $receipt")
                return
            }

            val electionId = connector.createdElectionId(receipt)

            if(electionId == null) {
                sendNotification(context, "Error Getting Election Id", "Transaction failed")
                isLoading.value = false
                Log.d("SmartContract", "Election error: $receipt")
                return
            }

            val result = user.addElectionCreatedToDB(
                electionId,
                electionTitle,
                startTime,
                endTime,
                userAddress.toString(),
                receipt.transactionHash
            )

            if(!result) {
                user.writeAuditLog(AUDIT.CREATE_ELECTION, STATUS.ERROR, receipt.transactionHash.toString())
                sendNotification(context, "Error Adding Election", "Failed to add election to database")
                isLoading.value = false
                showPreviewDialog = false
                return
            }

            user.writeAuditLog(AUDIT.CREATE_ELECTION, STATUS.SUCCESS, receipt.transactionHash.toString())

            sendNotification(context, "Election created successfully", "Transaction Success")
            isLoading.value = false
            onDismissRequest()
            Log.d("SmartContract", "Election created: $receipt")
        } catch (e: Exception) {
            Log.e("SmartContract", "Failed to create election", e)

            if(e.message?.contains("insufficient funds") == true) {
                sendNotification(context, "Insufficient funds", "Add Some tokens")
                return
            }
            isLoading.value = false
            sendNotification(context, "Failed to create election", "Retry Later")
        }

    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = Color.White,
    ) {
        Column(
            modifier = Modifier
                .padding(start =  10.dp, bottom = 10.dp, end = 10.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(15.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                "Create Election",
                fontSize = 20,
                fontWeight = FontWeight.Bold
            )

            TextField(
                value = electionTitle,
                onValueChange = { electionViewModel.setElectionTitle(it) },
                label = {
                    Text(
                        "Election Title",
                    )
                },
            )

            DateInputField(
                label = "Start Date",
                date = convertLongToDate(startTime),
                onDateSelected = {
                    onElectionStartTimeChange(it)
                },
            )

            DateInputField(
                label = "End Date",
                date = convertLongToDate(endTime),
                onDateSelected = {
                    onElectionEndTimeChange(it)
                },
            )

            if(isLoading.value) {
                Loader()
            } else {
                PrimaryButton(
                    text = "Create Election",
                    onClick = { handleCreateElectionPreviewOnly() },
                    enabled = !errorExist,
                    colors = ButtonColors(
                        containerColor = Color(0xFF1B1B1B),
                        contentColor = Color.White,
                        disabledContainerColor = Color.Gray,
                        disabledContentColor = Color.White
                    )
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
                            handleCreateElection()
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
}
