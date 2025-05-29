package com.example.voote.view.kyc

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationCity
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Streetview
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
import com.example.voote.ui.components.CTextButton
import com.example.voote.ui.components.Component
import com.example.voote.ui.components.Logo
import com.example.voote.ui.components.PrimaryButton

@Composable
fun AddressScreen(navController: NavController) {

    var country by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var street by remember { mutableStateOf("") }
    var postCode by remember { mutableStateOf("") }

    Scaffold {
            innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Logo()
            Component().Text(
                text = "Verify Address",
                fontSize = 30,
                fontWeight = FontWeight.Bold
            )

            Component().Text(
                text = "Provide your current residential address",
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
                AForm(
                    country = country,
                    onCountryChange = { country = it },
                    state = state,
                    onStateChange = { state = it },
                    city = city,
                    onCityChange = { city = it },
                    street = street,
                    onStreetChange = { street = it },
                    postCode = postCode,
                    onPostCodeChange = { postCode = it }
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                AButtons(
                    navController = navController
                )
            }
        }
    }
}

@Composable
fun AForm(
    country: String,
    onCountryChange: (String) -> Unit,
    state: String,
    onStateChange: (String) -> Unit,
    city: String,
    onCityChange: (String) -> Unit,
    street: String,
    onStreetChange: (String) -> Unit,
    postCode: String,
    onPostCodeChange: (String) -> Unit
) {
    Column (
        verticalArrangement = Arrangement.spacedBy(15.dp),
    ) {

        Component().TextField(
            value = country,
            onValueChange = onCountryChange,
            label = { Text(
                "Country",
            ) },
        )

        Component().TextField(
            value = state,
            onValueChange = onStateChange,
            label = { Text(
                "State",
            ) },
        )

        Component().TextField(
            value = city,
            onValueChange = onCityChange,
            label = { Text(
                "City",
            ) },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Outlined.LocationCity,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            },
        )

        Component().TextField(
            value = street,
            onValueChange = onStreetChange,
            label = { Text(
                "Street",
            ) },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Streetview,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            },
        )

        Component().TextField(
            value = postCode,
            onValueChange = onPostCodeChange,
            label = { Text(
                "Post Code",
            ) },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Place,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            },
        )

        CTextButton(
            text = "Autofill with location",
            onClick = {},
            modifier = Modifier.align(Alignment.End),
            color = Color(0x401B1B1B)
        )
    }
}

@Composable
fun AButtons(navController: NavController) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PrimaryButton(
            text = "Continue",
            onClick = {
                navController.navigate(Routes.FACE_VERIFICATION)
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