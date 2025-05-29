package com.example.voote.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.voote.model.data.CandidateInformation
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ElectionCandidateColumn(items: List<CandidateInformation>, isDynamic: Boolean = false) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()
    var showSheet by remember { mutableStateOf(false) }
    var selectedCandidate by remember { mutableStateOf<CandidateInformation?>(null) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(if (isDynamic) 1000.dp else 300.dp)
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(items.size) { index ->
                CandidateCard(
                    candidateImageUri = items[index].image,
                    candidateName = items[index].name,
                    onClick = {
                        selectedCandidate = items[index]
                        showSheet = true
                        coroutineScope.launch {
                            sheetState.show()
                        }
                    }
                )
            }
        }
    }

    if (showSheet && selectedCandidate != null) {
        CandidateModal(
            selectedCandidate = selectedCandidate,
            sheetState = sheetState,
            onDismissRequest = {
                showSheet = false
                selectedCandidate = null
            }
        )
    }
}
