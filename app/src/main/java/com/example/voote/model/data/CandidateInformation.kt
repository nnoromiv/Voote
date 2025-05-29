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
        image = Constants().imageUrl.toUri(),
        name = "John Berezanskii"
    ),
    CandidateInformation(
        image = Constants().imageUrl.toUri(),
        name = "John Berezanskii"
    ),
    CandidateInformation(
        image = Constants().imageUrl.toUri(),
        name = "John Berezanskii"
    ),
    CandidateInformation(
        image = Constants().imageUrl.toUri(),
        name = "John Berezanskii"
    ),
    CandidateInformation(
        image = Constants().imageUrl.toUri(),
        name = "John Doe"
    ),
    CandidateInformation(
        image = Constants().imageUrl.toUri(),
        name = "John Doe"
    ),
    CandidateInformation(
        image = Constants().imageUrl.toUri(),
        name = "John Berezanskii"
    ),
    CandidateInformation(
        image = Constants().imageUrl.toUri(),
        name = "John Berezanskii"
    ),
    CandidateInformation(
        image = Constants().imageUrl.toUri(),
        name = "John Berezanskii"
    ),
    CandidateInformation(
        image = Constants().imageUrl.toUri(),
        name = "John Berezanskii"
    ),
    CandidateInformation(
        image = Constants().imageUrl.toUri(),
        name = "John Doe"
    ),
    CandidateInformation(
        image = Constants().imageUrl.toUri(),
        name = "John Doe"
    ),
    CandidateInformation(
        image = Constants().imageUrl.toUri(),
        name = "John Berezanskii"
    ),
    CandidateInformation(
        image = Constants().imageUrl.toUri(),
        name = "John Berezanskii"
    ),
    CandidateInformation(
        image = Constants().imageUrl.toUri(),
        name = "John Berezanskii"
    ),
    CandidateInformation(
        image = Constants().imageUrl.toUri(),
        name = "John Berezanskii"
    ),
    CandidateInformation(
        image = Constants().imageUrl.toUri(),
        name = "John Doe"
    ),
    CandidateInformation(
        image = Constants().imageUrl.toUri(),
        name = "John Doe"
    ),

    )
