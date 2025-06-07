package com.example.voote.view.kyc.driverlicence

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.voote.navigation.ScanID
import com.example.voote.navigation.homeObject
import com.example.voote.ui.components.COutlinedButton
import com.example.voote.ui.components.PrimaryButton

@Composable
fun DriverLicenceButtons(navController: NavController) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PrimaryButton(
            text = "Scan Licence",
            onClick = {
                navController.navigate(ScanID(
                    documentType = "DL"
                ))
            },
            modifier = Modifier.padding(vertical = 10.dp)
        )

        COutlinedButton(
            text = "Do this later",
            onClick = {
                navController.navigate(homeObject)
            },
            modifier = Modifier.padding(vertical = 10.dp)
        )
    }
}