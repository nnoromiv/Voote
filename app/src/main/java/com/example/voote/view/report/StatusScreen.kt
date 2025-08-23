package com.example.voote.view.report

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.voote.firebase.data.STATUS
import com.example.voote.ui.components.PrimaryButton
import com.example.voote.ui.components.Text
import com.example.voote.navigation.toAppRoute

@Composable
fun StatusScreen(status: STATUS, nextScreen: String? = "", navController: NavController) {

    val color = when (status) {
        STATUS.SUCCESS -> Color(0xFF00BA11)
        STATUS.ERROR -> Color.Red
    }

    val text = when (status) {
        STATUS.SUCCESS -> "Business Handled"
        STATUS.ERROR -> "Error while handling business"
    }

    val imageVector = when (status) {
        STATUS.SUCCESS -> Icons.Outlined.Done
        STATUS.ERROR -> Icons.Outlined.Error
    }

    fun handleClick() {

        if(nextScreen.isNullOrEmpty()) {
            navController.popBackStack()
            return
        }

        val route = nextScreen.toAppRoute()

        navController.navigate(route)
    }

    Scaffold {
        innerPadding ->

        Column (
            modifier = Modifier
                .background(color)
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .fillMaxWidth(),
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector,
                        contentDescription = null,
                        modifier = Modifier.size(200.dp),
                        tint = Color.White
                    )

                    Text(
                        text,
                        fontSize = 18,
                        color = Color.White,
                        modifier = Modifier.padding(top = 20.dp).fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }

            PrimaryButton(
                text = "Continue",
                onClick = { handleClick() },
                enabled = true,
                colors = ButtonColors(
                    containerColor = Color.White,
                    contentColor = color,
                    disabledContainerColor = Color.Gray,
                    disabledContentColor = Color.White
                ),
            )
        }
    }
}