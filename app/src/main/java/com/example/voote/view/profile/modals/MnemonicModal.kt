package com.example.voote.view.profile.modals

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.voote.ui.components.PrimaryButton
import com.example.voote.ui.components.Text
import com.example.voote.utils.helpers.copyToClipboard
import com.example.voote.utils.helpers.decryptWithKeyStore
import com.example.voote.viewModel.WalletViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MnemonicModal(walletViewModel: WalletViewModel, sheetState: SheetState, onDismissRequest: () -> Unit) {

    val context = LocalContext.current
    val walletData by walletViewModel.walletData.collectAsState()
    val mnemonicWords = remember { mutableStateListOf<String>() }

    val walletMnemonic = walletData?.mnemonic ?: ""

    LaunchedEffect(walletMnemonic) {
        runCatching {
            val decrypted = decryptWithKeyStore(walletMnemonic)
            mnemonicWords.clear()
            mnemonicWords.addAll(decrypted.split(" "))
        }.onFailure {
            Log.e("ProfileScreen", "Error decrypting mnemonic", it)
            // Log error or show toast
        }
    }


    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = Color.White,
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)
                .verticalScroll(rememberScrollState())
                .heightIn(max = 400.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                "Mnemonic",
                fontSize = 18,
                fontWeight = FontWeight.Bold
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 10.dp)
            ) {
                itemsIndexed(mnemonicWords) { index, word ->
                    Text(
                        "${index + 1}. $word",
                        fontSize = 12,
                        fontWeight = FontWeight.Normal
                    )
                }
            }

            PrimaryButton(
                text = "Copy",
                onClick = {
                    copyToClipboard(
                        context,
                        "Mnemonic",
                        mnemonicWords.joinToString(" ")
                    )
                }
            )
        }
    }
}
