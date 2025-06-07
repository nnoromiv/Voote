package com.example.voote.view.kyc.identitynumber

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
fun IdentityNumberButtons(
    navController: NavController,
    isUsingPassport: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PrimaryButton(
            text =  if (isUsingPassport) "Scan Passport" else "Scan BRP",
            onClick = {
                val documentType = if (isUsingPassport) "passport" else "brp"
                navController.navigate(ScanID(
                    documentType
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