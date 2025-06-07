package com.example.voote.model.data

import android.net.Uri
import androidx.core.net.toUri
import com.example.voote.utils.Constants

data class CandidateInformation(
    val image: Uri,
    val name: String,
)

val candidateItem: List<CandidateInformation> = listOf(
    CandidateInformation(
        Constants().imageUrl.toUri(),
        "John Berezanskii"
    ),
    CandidateInformation(
        Constants().imageUrl.toUri(),
        "John Berezanskii"
    ),
    CandidateInformation(
        Constants().imageUrl.toUri(),
        "John Berezanskii"
    ),
    CandidateInformation(
        Constants().imageUrl.toUri(),
        "John Berezanskii"
    ),
    CandidateInformation(
        Constants().imageUrl.toUri(),
        "John Doe"
    ),
    CandidateInformation(
        Constants().imageUrl.toUri(),
        "John Doe"
    ),
    CandidateInformation(
        Constants().imageUrl.toUri(),
        "John Berezanskii"
    ),
    CandidateInformation(
        Constants().imageUrl.toUri(),
        "John Berezanskii"
    ),
    CandidateInformation(
        Constants().imageUrl.toUri(),
        "John Berezanskii"
    ),
    CandidateInformation(
        Constants().imageUrl.toUri(),
        "John Berezanskii"
    ),
    CandidateInformation(
        Constants().imageUrl.toUri(),
        "John Doe"
    ),
    CandidateInformation(
        Constants().imageUrl.toUri(),
        "John Doe"
    ),
    CandidateInformation(
        Constants().imageUrl.toUri(),
        "John Berezanskii"
    ),
    CandidateInformation(
        Constants().imageUrl.toUri(),
        "John Berezanskii"
    ),
    CandidateInformation(
        Constants().imageUrl.toUri(),
        "John Berezanskii"
    ),
    CandidateInformation(
        Constants().imageUrl.toUri(),
        "John Berezanskii"
    ),
    CandidateInformation(
        Constants().imageUrl.toUri(),
        "John Doe"
    ),
    CandidateInformation(
        Constants().imageUrl.toUri(),
        "John Doe"
    ),

    )
