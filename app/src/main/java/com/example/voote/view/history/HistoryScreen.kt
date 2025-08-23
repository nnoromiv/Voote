package com.example.voote.view.history

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.voote.ui.components.BottomNavigation
import com.example.voote.ui.components.Loader
import com.example.voote.ui.components.Text
import com.example.voote.utils.Constants
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import com.composables.icons.lucide.Blocks
import com.composables.icons.lucide.DoorOpen
import com.composables.icons.lucide.History
import com.composables.icons.lucide.LayoutDashboard
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.PersonStanding
import com.composables.icons.lucide.ShieldQuestion
import com.composables.icons.lucide.Vote
import com.example.voote.firebase.data.AUDIT
import com.example.voote.firebase.data.AuditLogEntry
import com.example.voote.firebase.data.STATUS
import com.example.voote.utils.helpers.openWebsite
import com.example.voote.viewModel.AuthViewModel
import com.example.voote.viewModel.HistoryViewModel
import com.example.voote.viewModel.WalletViewModel

@Composable
fun HistoryScreen(viewModel: HistoryViewModel = viewModel(), authManager: AuthViewModel, walletViewModel: WalletViewModel, navController: NavController) {

    val history by viewModel.auditLogs.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val isLoggedIn = authManager.isLoggedIn()

    val context = LocalContext.current
    val constant = Constants()

    var userBlockchainUrl by remember { mutableStateOf<String?>("") }

    val walletData by walletViewModel.walletData.collectAsState()

    LaunchedEffect(isLoggedIn) {
        viewModel.loadFirst50AuditLogs(isLoggedIn)
        userBlockchainUrl = constant.txHashUrl + "address/" + walletData?.address.toString()
    }

    Log.d("HistoryScreen", "User Blockchain URL: $history")

    Scaffold (
        bottomBar = { BottomNavigation(navController) }
    ) {
            innerPadding ->
        Column (
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            if(isLoading) {
                Loader()
            } else {
                if(history.isEmpty()) {
                    NoHistory()
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "History",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20
                        )

                        IconButton(
                            onClick = {
                                openWebsite(context, userBlockchainUrl!!)
                            }
                        ) {
                            Icon(
                                imageVector = Lucide.History,
                                contentDescription = null,
                            )
                        }
                    }
                    HistoryList(context, constant, history)
                }
            }
        }
    }
}

@Composable
fun HistoryList(context: Context, constant: Constants, history: List<AuditLogEntry>) {

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        items(history.count()) { logs ->

            val data = history[logs]

            val color = when(data.status) {
                STATUS.ERROR  -> Color.Red
                STATUS.SUCCESS -> Color.Green
            }

            val imageVector = when(data.action) {
                AUDIT.CREATE_ELECTION -> Lucide.Blocks
                AUDIT.CREATE_CANDIDATE -> Lucide.PersonStanding
                AUDIT.VOTE -> Lucide.Vote
                AUDIT.LOGIN -> Lucide.DoorOpen
                AUDIT.KYC -> Lucide.LayoutDashboard
                AUDIT.NONE -> Lucide.ShieldQuestion
            }

            val url = if(data.action == AUDIT.CREATE_ELECTION || data.action == AUDIT.CREATE_CANDIDATE || data.action == AUDIT.VOTE) {
                constant.txHashUrl + "tx/" + data.details
            } else{
                null
            }

            val intent = Intent(Intent.ACTION_VIEW, url?.toUri())

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                onClick = {
                    if(data.action == AUDIT.CREATE_ELECTION || data.action == AUDIT.CREATE_CANDIDATE || data.action == AUDIT.VOTE) {
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(intent)
                    } else {
                        // Do Nothing
                    }
                }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                        ) {
                            Row {
                                Icon(
                                    imageVector,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .padding(end = 10.dp)
                                        .size(20.dp)
                                )

                                Text(
                                    data.action.toString().replace("_", " ").lowercase().replaceFirstChar { it.uppercase() },
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20
                                )
                            }
                        }


                        Box(
                            modifier = Modifier
                                .size(10.dp) // Small circle size
                                .clip(CircleShape)
                                .background(color) // Use your dynamic color here
                        )

                    }

                    Spacer(modifier = Modifier.height(4.dp))

                }
            }
        }
    }
}

