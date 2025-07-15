package com.example.voote.view.modals

import android.Manifest
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
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
import com.example.voote.viewModel.ElectionViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.example.voote.ThisApplication
import com.example.voote.firebase.election.Election
import com.example.voote.firebase.user.User
import com.example.voote.model.data.ElectionData
import com.example.voote.ui.components.ElectionDropdownMenu
import com.example.voote.ui.components.ErrorHandlingInputField
import com.example.voote.ui.components.Loader
import com.example.voote.utils.helpers.isValidBlockchainAddress
import com.example.voote.utils.helpers.sendNotification
import com.example.voote.viewModel.AuthViewModel
import kotlinx.coroutines.launch

@RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterAddressToVote(authManager: AuthViewModel, sheetState: SheetState, onDismissRequest: () -> Unit) {
    val isLoading = remember { mutableStateOf(false) }
    val election = Election()
    val electionViewModel: ElectionViewModel = viewModel()
    val candidateAddress by electionViewModel.candidateAddress.collectAsState()
    val isIdError by electionViewModel.isIdError.collectAsState()
    val idErrorMessage by electionViewModel.idErrorMessage.collectAsState()

    var selectedElectionId by remember { mutableStateOf<Int?>(null) }
    var selectedId by remember { mutableStateOf<String?>(null) }
    val electionData  = remember { mutableListOf<ElectionData>() }

    val uid = authManager.userUid().toString()
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

    val errorExist = candidateAddress.isEmpty() || selectedId == null

    suspend fun handleRegisterAddress() {
        if (errorExist) {
            return
        }

        isLoading.value = true

        try {

            val result = election.addRegisteredAddressToElection(
                selectedId!!,
                candidateAddress
            )

            if(!result) {
                isLoading.value = false
                return
            }

            sendNotification(context, "Address registered successfully", "Transaction Success")

            onDismissRequest()
            isLoading.value = false
            return

        } catch (e: Exception) {
            Log.e("Address", "Failed to register address", e)
            isLoading.value = false
        }

    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = Color.White,
        modifier = Modifier.wrapContentHeight()
    ) {
            if(electionData.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Create an election to register an address",
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
                        "Register an Address",
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

                    ErrorHandlingInputField(
                        candidateAddress,
                        onValueChange = { onCandidateAddressChange(it.trim()) },
                        isError = isIdError,
                        errorMessage = idErrorMessage,
                        label = "Candidate Blockchain Address",
                        imageVector = Icons.Outlined.AddRoad
                    )

                    if(isLoading.value) {
                        Loader()
                    } else {
                        PrimaryButton(
                            text = "Register Address",
                            onClick = {
                                coroutineScope.launch {
                                    handleRegisterAddress()
                                }
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
        }
    }
}

