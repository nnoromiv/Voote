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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.voote.navigation.PersonalVerification
import com.example.voote.navigation.Profile
import kotlinx.coroutines.delay

@Composable
fun KYCReminder(
    navController: NavController,
    kycCompletionPercentage: Int
) {
    val isLoading = remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(1000)
        isLoading.value = false
    }

    if (isLoading.value) {
        Loader()
    } else {
        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth()
        ) {

            MyChip(
                text = "Tier I",
                kycCompletionPercentage,
                onClick = {
                    navController.navigate(Profile) {
                        launchSingleTop = true
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        restoreState = true
                    }
                }
            )

            MyChip(
                text = "$kycCompletionPercentage % Complete",
                kycCompletionPercentage,
                onClick = {
                    navController.navigate(PersonalVerification)
                }
            )
        }
    }
}

@Composable
fun MyChip(
    text: String,
    kycCompletionPercentage: Int,
    onClick: () -> Unit
) {
    SuggestionChip(
        onClick = { onClick() },
        label = {
            Text(
                text,
                color = if (kycCompletionPercentage == 100) Color.Black else Color.White,
                fontSize = 12,
                modifier = Modifier.wrapContentSize()
            )
        },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = if (kycCompletionPercentage == 100) Color(0x1B1B1B1B) else Color.Black,
        ),

        border = SuggestionChipDefaults.suggestionChipBorder(false),

        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.wrapContentSize().padding(end = 10.dp)
    )
}