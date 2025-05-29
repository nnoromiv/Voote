package com.example.voote.view.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.voote.R
import com.example.voote.navigation.Routes
import com.example.voote.ui.components.COutlinedButton
import com.example.voote.ui.components.PrimaryButton
import com.example.voote.utils.Constants

@Composable
fun MainScreen(navController: NavController) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(0.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(2f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 200.dp))
                    .background(
                        color = Color(0xFF1B1B1B).copy(alpha = 0.20f),
                    ),
            ) {
                TopActivity()
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            ) {
                BottomActivity(navController)
            }
        }
    }
}

@Composable
fun TopActivity() {
    Image(
        painter = painterResource(R.drawable.ic_background),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Constants().cFullSizeModifier,
    )

    Image(
        painter = painterResource(R.drawable.ic_launcher),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Constants().cFullSizeModifier,
    )
}

@Composable
fun BottomActivity(navController: NavController) {
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
                navController.navigate(Routes.LOGIN)
            },
        )

        COutlinedButton(
            text = stringResource(R.string.sign_up),
            onClick = {
                navController.navigate(Routes.SIGNUP)
            }
        )
    }
}
