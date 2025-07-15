@file:Suppress("DEPRECATION")
package com.example.voote.view.kyc.address

import android.app.Activity
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationCity
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Streetview
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.voote.ui.components.CTextButton
import com.example.voote.ui.components.Loader
import com.example.voote.ui.components.TextField
import com.example.voote.utils.helpers.getUserLocation
import com.example.voote.viewModel.AddressViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

@Composable
fun AddressForm() {
    val context = LocalContext.current
    val activity = LocalActivity.current as Activity
    var isLoading by remember { mutableStateOf(false) }

    val addressViewModel : AddressViewModel = viewModel()
    val country by addressViewModel.country.collectAsState()
    val state by addressViewModel.state.collectAsState()
    val city by addressViewModel.city.collectAsState()
    val street by addressViewModel.street.collectAsState()
    val postCode by addressViewModel.postCode.collectAsState()

    fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun handleAddressResult(addresses: List<Address>?) {

        if (!addresses.isNullOrEmpty()) {
            val address = addresses[0]
            var street = address.thoroughfare ?: address.subThoroughfare

            street = if (street.equals("Unnamed Road", ignoreCase = true)) {
                val fullAddress = address.getAddressLine(0) ?: ""
                // Remove "Unnamed Road" from the full address line and trim the result
                fullAddress.replace("Unnamed Road", "", ignoreCase = true).replace(",", "", ignoreCase = true).trim()
            } else {
                street ?: ""
            }

            addressViewModel.setCountry(address.countryName ?: "")
            addressViewModel.setState(address.adminArea ?: "")
            addressViewModel.setCity(address.subAdminArea ?: "")
            addressViewModel.setStreet(street)
            addressViewModel.setPostCode(address.postalCode ?: "")
            showToast("Location filled")
        } else {
            showToast("No address found")
        }
        isLoading = false
    }


    fun autofillAddress(lat: Double, long: Double) {
        isLoading = true

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    withContext(Dispatchers.Main) {
                        geocoder.getFromLocation(
                            lat, long, 1,
                            object : Geocoder.GeocodeListener {
                                override fun onGeocode(addresses: MutableList<Address>) {
                                    handleAddressResult(addresses)
                                    isLoading = false
                                }

                                override fun onError(errorMessage: String?) {
                                    showToast("Failed: $errorMessage")
                                    isLoading = false
                                }
                            }
                        )
                    }
                } else {
                    val addresses = geocoder.getFromLocation(lat, long, 1)
                    withContext(Dispatchers.Main) {
                        handleAddressResult(addresses)
                        isLoading = false
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showToast("Failed: ${e.message}")
                    isLoading = false
                }
            }
        }
    }

    Column (
        verticalArrangement = Arrangement.spacedBy(15.dp),
    ) {

        TextField(
            value = country,
            onValueChange = { addressViewModel.setCountry(it.trim()) },
            label = { Text(
                "Country",
            ) },
        )

        TextField(
            value = state,
            onValueChange = { addressViewModel.setState(it.trim()) },
            label = { Text(
                "State",
            ) },
        )

        TextField(
            value = city,
            onValueChange = { addressViewModel.setCity(it.trim()) },
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

        TextField(
            value = street,
            onValueChange = { addressViewModel.setStreet(it.trim()) },
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

        TextField(
            value = postCode,
            onValueChange = { addressViewModel.setPostCode(it.trim()) },
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

        if(isLoading) {
            Loader()
        } else {
            CTextButton(
                text = "Autofill with location",
                onClick = {
                    isLoading = true

                    getUserLocation(activity, context,
                        onLocationDetected = {location ->
                            val lat = location.latitude
                            val long = location.longitude
                            autofillAddress(lat, long)
                            isLoading = false
                        },
                            onError = {
                                isLoading = false
                                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                            }
                    )
                },
                modifier = Modifier.align(Alignment.End),
                color = Color(0x401B1B1B),
                enabled = !isLoading
            )
        }

    }
}
