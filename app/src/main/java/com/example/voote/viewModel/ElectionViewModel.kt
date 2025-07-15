package com.example.voote.viewModel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ElectionViewModel : ViewModel() {
    // Create Election fields
    private val _electionTitle = MutableStateFlow("")
    val electionTitle: StateFlow<String> = _electionTitle

    fun setElectionTitle(title: String) {
        _electionTitle.value = title
    }

    private val _startTime = MutableStateFlow(0L)
    val startTime: StateFlow<Long> = _startTime.asStateFlow()

    fun setStartTime(value: Long) {
        _startTime.value = value
    }

    private val _endTime = MutableStateFlow(0L)
    val endTime: StateFlow<Long> = _endTime.asStateFlow()

    fun setEndTime(value: Long) {
        _endTime.value = value
    }

    // Register Candidate fields
    private val _candidateName = MutableStateFlow("")
    val candidateName: StateFlow<String> = _candidateName

    fun setCandidateName(name: String) {
        _candidateName.value = name
    }


    private val _candidateAddress = MutableStateFlow("")
    val candidateAddress: StateFlow<String> = _candidateAddress

    fun setCandidateAddress(description: String) {
        _candidateAddress.value = description
    }

    private val _candidateBiographyLink = MutableStateFlow("")
    val candidateBiographyLink: StateFlow<String> = _candidateBiographyLink

    fun setCandidateBiographyLink(link: String) {
        _candidateBiographyLink.value = link
    }

    private val _candidateCampaignLink = MutableStateFlow("")
    val candidateCampaignLink: StateFlow<String> = _candidateCampaignLink

    fun setCandidateCampaignLink(link: String) {
        _candidateCampaignLink.value = link
    }

    private val _isIdError = MutableStateFlow(false)
    val isIdError: StateFlow<Boolean> = _isIdError

    fun setIsIdError(value: Boolean) {
        _isIdError.value = value
    }

    private val _idErrorMessage = MutableStateFlow("")
    val idErrorMessage: StateFlow<String> = _idErrorMessage

    fun setIdErrorMessage(value: String) {
        _idErrorMessage.value = value
    }

}