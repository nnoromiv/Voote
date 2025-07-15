package com.example.voote.model.data

import com.example.voote.firebase.data.TAG

data class ElectionData(
    val id: String = "",
    val title: String = "",
    val startTime: Long = 0L,
    val endTime: Long = 0L,
    val transactionHash: String = "",
    val createdBy: String = "",
    val tag : TAG = TAG.NONE,
    val electionId: Int = 0,
    val contractAddress: String = "",
    val registeredAddresses: List<String> = listOf()
)

data class CandidateData(
    val id: String = "",
    val candidateName: String = "",
    val electionId: Int = 0,
    val candidateImageUrl: String? = null,
    val candidateBiographyLink: String = "",
    val candidateCampaignLink: String = "",
    val transactionHash: String = "",
    val addedBy: String = "",
    val addedAt: Long = 0L,
    val candidateAddress: String = "",
    val candidateId: Int = 0,
)

