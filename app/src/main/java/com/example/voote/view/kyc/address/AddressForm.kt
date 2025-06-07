package com.example.voote.view.kyc.address

import android.content.Context
import android.location.Geocoder
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationCity
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Streetview
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.voote.ui.components.CTextButton
import com.example.voote.ui.components.Component
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

@Composable
fun AddressForm(
    context: Context,
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
    var itemIsLoading by remember { mutableStateOf(false) }

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

        if(itemIsLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.End),
                color = Color(0x401B1B1B),
                strokeWidth = 2.dp,
            )
        } else {
            CTextButton(
                text = "Autofill with location",
                onClick = {
                    itemIsLoading = true
                    handleAutofillLocation(context) { location ->
                        val lat = location.latitude
                        val long = location.longitude

                        // Run Geocoder in background thread
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                val geocoder = Geocoder(context, Locale.getDefault())
                                val addresses = geocoder.getFromLocation(lat, long, 1)

                                if (!addresses.isNullOrEmpty()) {
                                    val address = addresses[0]

                                    withContext(Dispatchers.Main) {
                                        onCountryChange(address.countryName ?: "")
                                        onStateChange(address.adminArea ?: "")
                                        onCityChange(address.locality ?: "")
                                        onStreetChange(address.thoroughfare ?: "")
                                        onPostCodeChange(address.postalCode ?: "")
                                        Toast.makeText(context, "Location filled", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    withContext(Dispatchers.Main) {
                                        Toast.makeText(context, "No address found", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } catch (e: Exception) {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(context, "Failed: ${e.message}", Toast.LENGTH_LONG).show()
                                }
                            } finally {
                                withContext(Dispatchers.Main) {
                                    itemIsLoading = false
                                }
                            }
                        }
                    }
                },
                modifier = Modifier.align(Alignment.End),
                color = Color(0x401B1B1B),
                enabled = !itemIsLoading
            )
        }

    }
}