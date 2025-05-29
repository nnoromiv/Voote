package com.example.voote.utils

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.example.voote.R

class Constants {

    val centuryFontFamily = FontFamily(
        Font(R.font.century_gothic, FontWeight.Normal),
        Font(R.font.century_gothic_bold, FontWeight.Bold),
        Font(R.font.century_gothic_bold_italic, FontWeight.Bold, FontStyle.Italic),
        Font(R.font.century_gothic_italic, FontWeight.Normal, FontStyle.Italic)
    )

    val cFullSizeModifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()

    val imageUrl = "https://images.unsplash.com/photo-1561948955-570b270e7c36?q=80&w=1201&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"

    val biographyText = "After graduating, he worked as a community organizer in Chicago, a civil rights lawyer and a professor of constitutional law at the University of Chicago. His political career began in the Illinois state legislature, where he served from 1997 to 2004.\n" +
            "\n" +
            "As president, he promoted a series of significant reforms, including the Affordable  Care Act (Obamacare), reforms in the  financial system and the end of the “Don't Ask,  Don't Tell” policy in the armed forces. He also ordered the operation that resulted in the  death of Osama bin Laden in 2011"

    val campaignText = "Pledge to provide affordable health insurance to all Americans, resulting in the implementation of the Affordable Care Act.\n" +
            "\n" +
            "Pledge to withdraw American troops from Iraq in a responsible manner, with a view to ending the war safely.\n" +
            "\n" +
            "Pledge to invest in renewable energy and clean energy technologies in order to reduce US dependence on foreign oil and combat climate change."
}
