package com.example.voote.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.voote.firebase.election.Election
import com.example.voote.model.data.CandidateData
import com.example.voote.model.data.ElectionData
import com.example.voote.utils.helpers.getElectionWithEarliestEndDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val election: Election,
    private val userViewModel: UserViewModel,
    private val walletViewModel: WalletViewModel
) : ViewModel() {

    val isLoading = MutableStateFlow(false)
    val allElectionData = MutableStateFlow<List<ElectionData>>(emptyList())
    val candidatesMap = MutableStateFlow<Map<String, List<CandidateData>>>(emptyMap())
    val cardData = MutableStateFlow<ElectionData?>(null)

    var name: String? = null
    var userName: String? = null
    var userAddress: String? = null

    fun loadData(isLoggedIn: Boolean) {
        if (!isLoggedIn) return

        viewModelScope.launch {
            isLoading.value = true
            val walletData = walletViewModel.walletData.value

            userAddress = walletData?.address.orEmpty()

            val userData = userViewModel.userData.value
            name = "${userData?.firstName} ${userData?.lastName}"
            userName = (userData?.lastName + userData?.uid?.substring(0, 4)).lowercase()

            val result = election.getRandomFiveElections()

            if (result.isNotEmpty()) {
                allElectionData.value = result

                val map = mutableMapOf<String, List<CandidateData>>()
                for (elections in result) {
                    val candidates = election.getCandidatesForElection(elections.id)
                    map[elections.id] = candidates
                }
                candidatesMap.value = map
            }

            val forElectionCard = election.getAllElections()
            cardData.value = getElectionWithEarliestEndDate(forElectionCard)

            isLoading.value = false
        }
    }
}
