package com.example.voote.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.voote.navigation.RoutePersonalVerification
import com.example.voote.navigation.RouteProfile

@Composable
fun KYCReminder(kycCompletionPercentage: Int, navController: NavController) {

    val tierText = when(kycCompletionPercentage) {
        in 0..25 -> "Tier I"
        in 26..50 -> "Tier II"
        in 51..80 -> "Tier III"
        else -> "Tier IV"
    }

    Row(
        horizontalArrangement = Arrangement.End,
        modifier = Modifier.fillMaxWidth()
    ) {

        MyChip(
            text = tierText,
            kycCompletionPercentage,
            onClick = {
                navController.navigate(RouteProfile) {
                    launchSingleTop = true
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    restoreState = true
                }
            }
        )

        if(kycCompletionPercentage < 100) {
            MyChip(
                text = "$kycCompletionPercentage % Complete",
                kycCompletionPercentage,
                onClick = {
                    navController.navigate(RoutePersonalVerification)
                }
            )
        }
    }
}

@Composable
fun MyChip(text: String, kycCompletionPercentage: Int, onClick: () -> Unit) {

    val containerColor = when(kycCompletionPercentage) {
        in 0..25 -> Color.Red
        in 26..50 -> Color(0xFF8D9C02)
        in 51..80 -> Color(0xFF00BA11)
        else -> Color(0xFF6D8E79)
    }

    SuggestionChip(
        onClick = { onClick() },
        label = {
            Text(
                text,
                color = Color.White,
                fontSize = 12,
                modifier = Modifier.wrapContentSize()
            )
        },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = containerColor,
        ),

        border = SuggestionChipDefaults.suggestionChipBorder(false),

        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.wrapContentSize().padding(end = 10.dp)
    )
}