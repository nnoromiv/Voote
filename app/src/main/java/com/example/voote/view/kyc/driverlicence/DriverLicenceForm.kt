package com.example.voote.view.kyc.driverlicence

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DriveEta
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.voote.ui.components.Component

@Composable
fun DriverLicenceForm(
    licenceNumber: String,
    onIdNumberChange: (String) -> Unit
) {
    Column (
        verticalArrangement = Arrangement.spacedBy(15.dp),
    ) {

        Component().TextField(
            value = licenceNumber,
            onValueChange = onIdNumberChange,
            label = { Text(
                "Driver's Licence Number",
            ) },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Outlined.DriveEta,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            },
        )
    }
}