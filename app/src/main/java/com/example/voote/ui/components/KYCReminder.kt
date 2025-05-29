package com.example.voote.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ChipColors
import androidx.compose.material3.SuggestionChip
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.voote.navigation.Routes

@Composable
fun KYCReminder(
    navController: NavController
) {
    Row(
        horizontalArrangement = Arrangement.End,
        modifier = Modifier.fillMaxWidth()
    ) {
        SuggestionChip(
            onClick = { /*TODO*/ },
            label = {
                Component().Text(
                    "Tier 1",
                    color = Color.White,
                    fontSize = 12,
                    modifier = Modifier.wrapContentSize()
                )
            },
            enabled = false,
            colors = ChipColors(
                labelColor = Color.Black,
                disabledLabelColor = Color.White,
                containerColor = Color.White,
                leadingIconContentColor = Color.White,
                trailingIconContentColor = Color.White,
                disabledContainerColor = Color.Black,
                disabledLeadingIconContentColor = Color.White,
                disabledTrailingIconContentColor = Color.White,
            ),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.wrapContentSize().padding(end = 10.dp)
        )

        SuggestionChip(
            onClick = {
                navController.navigate(Routes.PERSONAL_VERIFICATION)
            },
            label = {
                Component().Text(
                    "30% Complete",
                    color = Color.White,
                    fontSize = 12,
                    modifier = Modifier.wrapContentSize()
                )
            },
            enabled = false,
            colors = ChipColors(
                labelColor = Color.Black,
                disabledLabelColor = Color.White,
                containerColor = Color.White,
                leadingIconContentColor = Color.White,
                trailingIconContentColor = Color.White,
                disabledContainerColor = Color.Black,
                disabledLeadingIconContentColor = Color.White,
                disabledTrailingIconContentColor = Color.White,
            ),
            shape = RoundedCornerShape(20.dp),
        )
    }
}