package com.example.voote.view.kyc.identitynumber

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PermIdentity
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.voote.ui.components.COutlinedButton
import com.example.voote.ui.components.Component
import com.example.voote.ui.components.PrimaryButton

@Composable
fun IdentityNumberForm(
    idNumber: String,
    onIdNumberChange: (String) -> Unit,
    isUsingPassport: Boolean,
    onIsUsingPassportChange: () -> Unit = {}
) {

    Column (
        verticalArrangement = Arrangement.spacedBy(15.dp),
    ) {
        Row (
            modifier = Modifier.padding(vertical = 5.dp)
        ) {
            PrimaryButton(
                text = "Passport",
                onClick = onIsUsingPassportChange,
                modifier = Modifier.height(40.dp).width(150.dp),
                shape = RoundedCornerShape(
                    topStart = 10.dp,
                    bottomStart = 10.dp
                ),
                colors = if (!isUsingPassport) ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color(0xFF1B1B1B),
                    disabledContentColor = Color.White
                ) else null,

                border = if (!isUsingPassport) ButtonDefaults.outlinedButtonBorder(true) else null

            )
            COutlinedButton(
                text = "BRP",
                onClick = onIsUsingPassportChange,
                modifier = Modifier.height(40.dp).width(150.dp),
                shape = RoundedCornerShape(
                    topEnd = 10.dp,
                    bottomEnd = 10.dp
                ),
                colors = if (!isUsingPassport) ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1B1B1B),
                    contentColor = Color.White,
                    disabledContainerColor = Color.Gray,
                    disabledContentColor = Color(0xFF1B1B1B)
                ) else null,

                border = if (isUsingPassport) ButtonDefaults.outlinedButtonBorder(true) else null

            )
        }
        Component().TextField(
            value = idNumber,
            onValueChange = onIdNumberChange,
            label = { Text(
                if (isUsingPassport) "Passport Number" else "BRP Number",
            ) },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Outlined.PermIdentity,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            },
        )
    }
}