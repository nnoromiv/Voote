package com.example.voote.view.kyc.address

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.voote.ui.components.Component
import com.example.voote.ui.components.Logo
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource

@Composable
fun AddressScreen(navController: NavController) {

    var country by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var street by remember { mutableStateOf("") }
    var postCode by remember { mutableStateOf("") }

    val context = LocalContext.current

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
                AddressForm(
                    context,
                    country,
                    onCountryChange = { country = it },
                    state,
                    onStateChange = { state = it },
                    city,
                    onCityChange = { city = it },
                    street,
                    onStreetChange = { street = it },
                    postCode,
                    onPostCodeChange = { postCode = it }
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                AddressButton(
                    navController = navController
                )
            }
        }
    }
}

fun handleAutofillLocation(
    context: Context,
    onLocationDetected: (Location) -> Unit
) {
    val activity = context as Activity
    var permissionState = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)

    if(permissionState != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1001)
        return
    }

    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    fusedLocationClient.getCurrentLocation(
        Priority.PRIORITY_HIGH_ACCURACY,
        CancellationTokenSource().token)
        .addOnSuccessListener { location ->
            if(location != null) {
                onLocationDetected(location)
            }
            else {
                Toast.makeText(context, "Location is unavailable", Toast.LENGTH_SHORT).show()
            }
        }
        .addOnFailureListener {
            Toast.makeText(context, "Failed to get location", Toast.LENGTH_SHORT).show()
        }
}
