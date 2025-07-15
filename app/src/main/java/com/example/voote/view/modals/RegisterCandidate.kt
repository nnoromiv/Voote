package com.example.voote.view.modals

import android.Manifest
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddRoad
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.voote.ui.components.PrimaryButton
import com.example.voote.ui.components.Text
import com.example.voote.ui.components.TextField
import com.example.voote.viewModel.ElectionViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import com.example.voote.ThisApplication
import com.example.voote.blockchain.Connector
import com.example.voote.contract.Voote
import com.example.voote.firebase.data.Status
import com.example.voote.firebase.data.VotePreview
import com.example.voote.firebase.user.User
import com.example.voote.model.data.ElectionData
import com.example.voote.ui.components.ElectionDropdownMenu
import com.example.voote.ui.components.ErrorHandlingInputField
import com.example.voote.ui.components.ImagePicker
import com.example.voote.ui.components.Loader
import com.example.voote.ui.components.TransactionPreviewDialog
import com.example.voote.utils.helpers.isValidBlockchainAddress
import com.example.voote.utils.helpers.sendNotification
import com.example.voote.viewModel.AuthViewModel
import com.example.voote.viewModel.WalletViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterCandidate(contract: Voote, userAddress: String?, authManager: AuthViewModel, walletViewModel: WalletViewModel, sheetState: SheetState, onDismissRequest: () -> Unit) {
    val isLoading = remember { mutableStateOf(false) }
    val electionViewModel: ElectionViewModel = viewModel()
    val candidateName by electionViewModel.candidateName.collectAsState()
    val candidateAddress by electionViewModel.candidateAddress.collectAsState()
    val candidateBiographyLink by electionViewModel.candidateBiographyLink.collectAsState()
    val candidateCampaignLink by electionViewModel.candidateCampaignLink.collectAsState()
    val isIdError by electionViewModel.isIdError.collectAsState()
    val idErrorMessage by electionViewModel.idErrorMessage.collectAsState()

    var selectedElectionId by remember { mutableStateOf<Int?>(null) }
    var selectedId by remember { mutableStateOf<String?>(null) }
    val electionData  = remember { mutableListOf<ElectionData>() }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val connector = Connector(authManager, walletViewModel)
    var showPreviewDialog by remember { mutableStateOf(false) }
    val contractAddress = contract.contractAddress.toString()
    val uid = authManager.userUid().toString()
    val fromAddress = userAddress.toString()
    val gasData = remember { mutableStateOf(VotePreview(null, null, null)) }
    val user = User(uid)
    val context = LocalContext.current


    LaunchedEffect(Unit) {
        isLoading.value = true
        val result = user.getUserElections(onlyActiveElections = true)

        if (result.isEmpty()) {
            Log.d("Firestore", "User has no elections")
            isLoading.value = false
            return@LaunchedEffect
        }

        electionData.addAll(result)
        isLoading.value = false
    }

    val coroutineScope = (context.applicationContext as ThisApplication).appScope

    fun onCandidateAddressChange(value: String) {
        val isValid = isValidBlockchainAddress(value.trim())
        electionViewModel.setIsIdError(!isValid)
        electionViewModel.setIdErrorMessage(if (isValid) "" else "Invalid Blockchain Address e.g(0x123....)")
        electionViewModel.setCandidateAddress(value.trim())
    }

    val errorExist = candidateName.isEmpty() || candidateAddress.isEmpty() || selectedElectionId  == null || selectedImageUri == null || selectedId == null || candidateBiographyLink.isEmpty() || candidateCampaignLink.isEmpty() || candidateBiographyLink.isEmpty()

    fun handleRegisterCandidatePreviewOnly() {
        if (errorExist) {
            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val electionId = selectedElectionId!!.toBigInteger()

        coroutineScope.launch {
            try {

                val previewResult = withContext(Dispatchers.IO) {
                    connector.previewRegisterCandidate(electionId, candidateName, candidateAddress)
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

    suspend fun handleRegisterCandidate() {
        if(errorExist) {
            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val electionId = selectedElectionId!!.toBigInteger()

        isLoading.value = true

        try {
            showPreviewDialog = false

            val receipt = withContext(Dispatchers.IO) {
                contract.registerCandidate(electionId, candidateName, candidateAddress).send()
            }

            if(!receipt.isStatusOK) {
                sendNotification(context, "Error Registering Candidate", "Transaction failed")
                isLoading.value = false
                Log.d("SmartContract", "Election error: $receipt")
                return
            }

            val candidateId = connector.createdCandidateId(receipt)

            if(candidateId == null) {
                sendNotification(context, "Error Getting Candidate Id", "Transaction failed")
                isLoading.value = false
                Log.d("SmartContract", "Election error: $receipt")
                return
            }

            val result = user.addCandidateToElection(
                selectedId!!,
                selectedElectionId!!,
                candidateName,
                candidateAddress,
                selectedImageUri.toString(),
                candidateBiographyLink,
                candidateCampaignLink,
                receipt.transactionHash,
                candidateId
            )

            if(result.isEmpty()) {
                sendNotification(context, "Error Registering Candidate", "Failed to register candidate")
                isLoading.value = false
                showPreviewDialog = false
                return
            }

            val uploadResult = user.uploadCandidateImage(result, selectedImageUri!!, candidateAddress)

            if(uploadResult.status == Status.ERROR) {
                sendNotification(context, "Error Uploading Image", "Failed to upload image")
                isLoading.value = false
                showPreviewDialog = false
                return
            }

            sendNotification(context, "Candidate registered successfully", "Transaction Success")
            isLoading.value = false
            onDismissRequest()
            Log.d("SmartContract", "Candidate Registered: $receipt")
        } catch (e: Exception) {
            Log.e("SmartContract", "Failed to register candidate to election", e)

            if(e.message?.contains("insufficient funds") == true) {
                sendNotification(context, "Insufficient funds", "Add Some tokens")
                return
            }

            isLoading.value = false
            sendNotification(context, "Failed to register candidate to election", "Retry Later")
        }

    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = Color.White,
        modifier = Modifier.wrapContentHeight()
    ) {
        if(isLoading.value){
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp),
                contentAlignment = Alignment.Center
            ) {
                Loader()
            }
        } else {
            if(electionData.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Create an election to register a candidate",
                        fontSize = 16,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .padding(start =  10.dp, bottom = 10.dp, end = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(15.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        "Register a Candidate",
                        fontSize = 20,
                        fontWeight = FontWeight.Bold
                    )

                    ElectionDropdownMenu(
                        elections = electionData.toList(),
                        selectedElectionId = selectedElectionId,
                        onElectionSelected = {
                            selectedElectionId = it.electionId
                            selectedId = it.id
                        }
                    )

                    TextField(
                        value = candidateName,
                        onValueChange = { electionViewModel.setCandidateName(it) },
                        label = {
                            Text(
                                "Candidate Name",
                            )
                        },
                    )

                    ErrorHandlingInputField(
                        candidateAddress,
                        onValueChange = { onCandidateAddressChange(it.trim()) },
                        isError = isIdError,
                        errorMessage = idErrorMessage,
                        label = "Candidate Blockchain Address",
                        imageVector = Icons.Outlined.AddRoad
                    )

                    Row (
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.weight(1f)
                        ) {
                            TextField(
                                value = candidateBiographyLink,
                                onValueChange = { electionViewModel.setCandidateBiographyLink(it) },
                                label = {
                                    Text(
                                        "Biography Link",
                                    )
                                },
                                singleLine = true
                            )
                        }

                        Box(
                            modifier = Modifier.weight(1f)
                        ) {
                            TextField(
                                value = candidateCampaignLink,
                                onValueChange = { electionViewModel.setCandidateCampaignLink(it) },
                                label = {
                                    Text(
                                        text = "Campaign Link",
                                    )
                                },
                                singleLine = true
                            )
                        }
                    }

                    ImagePicker(
                        label = "Select Candidate Image"
                    ) { uri ->
                        selectedImageUri = uri
                    }

                    selectedImageUri?.let { uri ->
                        AsyncImage(
                            model = uri,
                            contentDescription = "Selected Image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }

                    if(isLoading.value) {
                        Loader()
                    } else {
                        PrimaryButton(
                            text = "Register Candidate",
                            onClick = {
                                handleRegisterCandidatePreviewOnly()
                            },
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
                                    handleRegisterCandidate()
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
    }
}

