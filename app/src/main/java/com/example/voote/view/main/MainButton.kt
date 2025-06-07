package com.example.voote.view.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.voote.R
import com.example.voote.navigation.Login
import com.example.voote.navigation.Signup
import com.example.voote.ui.components.COutlinedButton
import com.example.voote.ui.components.PrimaryButton
import com.example.voote.utils.Constants

@Composable
fun MainButton(navController: NavController) {
    Column(
        modifier = Constants().cFullSizeModifier.padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = stringResource(R.string.easy_online_election),
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = Constants().centuryFontFamily,
            color = Color.Black,
        )
        Text(
            text = stringResource(R.string.main_screen_sub_text),
            fontSize = 16.sp,
            fontFamily = Constants().centuryFontFamily,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        PrimaryButton(
            text = stringResource(R.string.login),
            onClick = {
                navController.navigate(Login)
            },
        )

        COutlinedButton(
            text = stringResource(R.string.sign_up),
            onClick = {
                navController.navigate(Signup)
            }
        )
    }
}
