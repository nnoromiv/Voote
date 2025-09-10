@file:Suppress("DEPRECATION")
package com.example.voote.view.kyc.address

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.voote.ui.components.TextField
import com.example.voote.viewModel.AddressViewModel

@Composable
fun AddressForm() {

    val addressViewModel : AddressViewModel = viewModel()
    val country by addressViewModel.country.collectAsState()
    val state by addressViewModel.state.collectAsState()
    val city by addressViewModel.city.collectAsState()
    val street by addressViewModel.street.collectAsState()
    val postCode by addressViewModel.postCode.collectAsState()

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
//
//        if(isLoading) {
//            Loader()
//        } else {
//            CTextButton(
//                text = "Autofill with location",
//                onClick = {
//                    isLoading = true
//
//                    getUserLocation(activity, context,
//                        onLocationDetected = {location ->
//                            val lat = location.latitude
//                            val long = location.longitude
//                            autofillAddress(lat, long)
//                            isLoading = false
//                        },
//                            onError = {
//                                isLoading = false
//                                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
//                            }
//                    )
//                },
//                modifier = Modifier.align(Alignment.End),
//                color = Color(0x401B1B1B),
//                enabled = !isLoading
//            )
//        }

    }
}
