package com.example.voote.utils

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.example.voote.R

class Constants {

    val passportRegex = "^[A-Z0-9]{6,10}$".toRegex()
    val driverLicenceRegex =  "^[A-Z0-9]{5,18}$".toRegex()

    val centuryFontFamily = FontFamily(
        Font(R.font.century_gothic, FontWeight.Normal),
        Font(R.font.century_gothic_bold, FontWeight.Bold),
        Font(R.font.century_gothic_bold_italic, FontWeight.Bold, FontStyle.Italic),
        Font(R.font.century_gothic_italic, FontWeight.Normal, FontStyle.Italic)
    )

    val cFullSizeModifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()

    val txHashUrl = "https://polygonscan.com/"
//    val testNetTxHashUrl = "https://sepolia.etherscan.io/"

    val vooteContractAddress = "0xBE674B757c2a258d2aAd3843F3128b9CF0E898aa"
//    val testNetVooteContractAddress = "0x43dBDd94d852a7a0cce8996447Ab74De0B8D93BE"

    val chainId = 137L
    val rpcUrl = "https://polygon-mainnet.g.alchemy.com/v2/FnpKmFie5JVqo8Rwgso0veb6UzKUpSDu"
// val testNetRpcUrl = "https://eth-sepolia.g.alchemy.com/v2/FnpKmFie5JVqo8Rwgso0veb6UzKUpSDu"


}
