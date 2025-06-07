package com.example.voote.view.election

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.voote.model.data.candidateItem
import com.example.voote.ui.components.Component
import com.example.voote.ui.components.CountDown
import com.example.voote.ui.components.ElectionCandidateColumn

@Composable
fun DynamicElectionScreen() {
    Scaffold {
        innerPadding ->
        Column(
            modifier = Modifier.fillMaxWidth().padding(innerPadding).padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Component().Text(
                text = "Presidential Election 2025",
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
                    CountCard()
                }
                Box(
                    modifier = Modifier.weight(1f)
                ){
                    CountCard()
                }
            }

            CountCard()

            CountDown(
                text = "Presidential Election 2025",
                totalTime = 1000,
            ).Card()

            ElectionCandidateColumn(
                items = candidateItem,
                isDynamic = true
            )
        }
    }
}


