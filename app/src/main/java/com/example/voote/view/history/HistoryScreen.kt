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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
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
import com.example.voote.firebase.data.TAG
import com.example.voote.firebase.user.User
import com.example.voote.model.data.ElectionData
import com.example.voote.ui.components.BottomNavigation
import com.example.voote.ui.components.Loader
import com.example.voote.ui.components.Text
import com.example.voote.utils.Constants
import com.example.voote.utils.helpers.convertLongToDate
import com.example.voote.viewModel.AuthViewModel
import androidx.core.net.toUri
import com.composables.icons.lucide.Blocks
import com.composables.icons.lucide.History
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Vote
import com.example.voote.utils.helpers.openWebsite
import com.example.voote.viewModel.WalletViewModel

@Composable
fun HistoryScreen(authManager: AuthViewModel, walletViewModel: WalletViewModel, navController: NavController) {

    val context = LocalContext.current
    val constant = Constants()
    val uid = authManager.userUid().toString()
    val user = User(uid)
    val electionData  = remember { mutableListOf<ElectionData?>() }
    val isLoading = remember { mutableStateOf(false) }

    var userBlockchainUrl by remember { mutableStateOf<String?>("") }

    val walletData by walletViewModel.walletData.collectAsState()

    LaunchedEffect(Unit) {
        userBlockchainUrl = constant.txHashUrl + "address/" + walletData?.address.toString()
    }

    LaunchedEffect(Unit) {
        isLoading.value = true
        val result = user.getUserElections()

        if (result.isEmpty()) {
            Log.d("Firestore", "User has no elections")
            isLoading.value = false
            return@LaunchedEffect
        }

        electionData.addAll(result)
        isLoading.value = false
    }

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
            if(isLoading.value) {
                Loader()
            } else {
                if(electionData.isEmpty()) {
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
                    HistoryList(context, constant, electionData)
                }
            }
        }
    }
}

@Composable
fun HistoryList(context: Context, constant: Constants, electionData: List<ElectionData?>) {

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        items(electionData.count()) { election ->

            val data = electionData[election] ?: return@items
            val currentTime = System.currentTimeMillis()

            val color = when {
                currentTime < data.startTime -> Color.Red
                currentTime in data.startTime..data.endTime -> Color.Yellow
                else -> Color.Green
            }

            val start = if (currentTime < data.startTime) {
                "Starts - ${convertLongToDate(data.startTime)}"
            } else {
                "Started - ${convertLongToDate(data.startTime)}"
            }

            val end = if (currentTime < data.endTime) {
                "Ends - ${convertLongToDate(data.endTime)}"
            } else {
                "Ended - ${convertLongToDate(data.endTime)}"
            }

            val imageVector = when(data.tag) {
                TAG.ELECTION -> Lucide.Blocks
                TAG.VOTE -> Lucide.Vote
                else -> Icons.Default.Home
            }

            val url = constant.txHashUrl + "tx/" + data.transactionHash
            val intent = Intent(Intent.ACTION_VIEW, url.toUri())

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                onClick = {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
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
                                    data.title,
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

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 5.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            start,
                            fontSize = 12,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            end,
                            fontSize = 12,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

