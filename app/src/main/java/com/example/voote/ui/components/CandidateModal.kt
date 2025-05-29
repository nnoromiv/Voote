package com.example.voote.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Approval
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.example.voote.R
import com.example.voote.model.data.CandidateInformation
import com.example.voote.utils.Constants

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun CandidateModal(
    selectedCandidate: CandidateInformation?,
    sheetState: SheetState,
    onDismissRequest: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = Color.White,
    ) {
        Column (
            modifier = Modifier.padding(horizontal =  10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ProfileBar(
                name = selectedCandidate!!.name,
                username = "Republic Party",
                userImageUri = Constants().imageUrl.toUri(),
                imageVector = Icons.Outlined.Approval
            )
            Socials()

            Body(
                title = "Biography",
                writeUpText = Constants().biographyText
            )

            Body(
                title = "Campaign",
                writeUpText = Constants().campaignText
            )

            PrimaryButton(
                text = "Vote ${selectedCandidate.name}",
                onClick = { /*TODO*/ }
            )
        }

    }
}

@Composable
fun Socials() {
    Box(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        contentAlignment = Alignment.CenterEnd
    ) {
        Row (
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                painter = painterResource(R.drawable.facebook),
                contentDescription = "Facebook",
                modifier = Modifier.size(24.dp)
            )
            Icon(
                painter = painterResource(R.drawable.x_twitter),
                contentDescription = "X f.k.a Twitter",
                modifier = Modifier.size(24.dp)
            )
            Icon(
                painter = painterResource(R.drawable.linkedin),
                contentDescription = "LinkedIn",
                modifier = Modifier.size(24.dp)
            )
            Icon(
                painter = painterResource(R.drawable.blog),
                contentDescription = "Website",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun Body(
    title: String,
    writeUpText: String
) {
    Column (
        modifier = Modifier.padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Component().Text(
            text = title,
            fontSize = 18,
            fontWeight = FontWeight.Bold
        )

        Component().Text(
            text = writeUpText,
            fontSize = 15,
            softWrap = true
        )
    }
}