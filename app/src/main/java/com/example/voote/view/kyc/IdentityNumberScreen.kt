package com.example.voote.view.kyc

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PermIdentity
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.voote.navigation.Routes
import com.example.voote.ui.components.COutlinedButton
import com.example.voote.ui.components.Component
import com.example.voote.ui.components.Logo
import com.example.voote.ui.components.PrimaryButton

@Composable
fun IdentityNumberScreen(navController: NavController) {
    var idNumber by remember { mutableStateOf("") }

    Scaffold {
            innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding).padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Logo()
            Component().Text(
                text = "Identity Number",
                fontSize = 30,
                fontWeight = FontWeight.Bold
            )
            Component().Text(
                text = "Verification",
                fontSize = 30,
                fontWeight = FontWeight.Bold,
            )
            Component().Text(
                text = "Provide a Passport Number or BRP Number",
                fontSize = 16,
                fontWeight = FontWeight.Normal,
                softWrap = true,
            )

            HorizontalDivider(
                color = Color(0x401B1B1B),
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                IDForm(
                    idNumber = idNumber,
                    onIdNumberChange = { idNumber = it }
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                INButtons(
                    navController = navController
                )
            }
        }
    }
}

@Composable
fun IDForm(
    idNumber: String,
    onIdNumberChange: (String) -> Unit
) {
    var isUsingPassport by remember { mutableStateOf(true) }

    Column (
        verticalArrangement = Arrangement.spacedBy(15.dp),
    ) {
        Row (
            modifier = Modifier.padding(vertical = 5.dp)
        ) {
            PrimaryButton(
                text = "Passport",
                onClick = {
                    isUsingPassport = true
                },
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
                onClick = {
                    isUsingPassport = false
                },
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

@Composable
fun INButtons(navController: NavController) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PrimaryButton(
            text = "Continue",
            onClick = {
                navController.navigate(Routes.DRIVER_LICENCE)
            },
            modifier = Modifier.padding(vertical = 10.dp)
        )

        COutlinedButton(
            text = "Do this later",
            onClick = {},
            modifier = Modifier.padding(vertical = 10.dp)
        )
    }
}