package com.example.voote.firebase.election

import android.util.Log
import com.example.voote.model.data.CandidateData
import com.example.voote.model.data.ElectionData
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class Election() {

    val db = Firebase.firestore
    val electionRef = db.collection("elections")
    val candidateRef = db.collection("candidates")

    suspend fun getAllElections(): List<ElectionData> {
        return try {
            val today = System.currentTimeMillis()
            electionRef.get().await()
                .documents
                .mapNotNull { doc ->
                    try {
                        doc.toObject(ElectionData::class.java)?.copy(id = doc.id)
                    } catch (e: Exception) {
                        Log.e("ElectionFetch", "Failed to deserialize election ${doc.id}", e)
                        null
                    }
                }
                .filter { it.endTime > today }
        } catch (e: Exception) {
            Log.e("ElectionFetch", "Error fetching elections", e)
            emptyList()
        }
    }

    suspend fun getCandidatesForElection(id: String): List<CandidateData> {
        return try {
            val snapshot = candidateRef
                .whereEqualTo("id", id)
                .get()
                .await()

            if (snapshot.isEmpty) {
                emptyList()
            } else {
                snapshot.documents.mapNotNull { doc ->
                    doc.toObject(CandidateData::class.java)?.copy(id = doc.id)
                }
            }
        } catch (e: Exception) {
            Log.e("CandidateFetch", "Error fetching candidates", e)
            emptyList()
        }
    }

    suspend fun getElectionData(id: String): ElectionData? {
        return try {
            val snapshot = electionRef
                .document(id)
                .get()
                .await()

            if (!snapshot.exists()) {
                null
            } else {
                snapshot.toObject(ElectionData::class.java)
                    ?.copy(id = snapshot.id)
            }
        } catch (e: Exception) {
            Log.e("ElectionFetch", "Error fetching election", e)
            null
        }
    }

    suspend fun getAllElectionsSortedByTitle(): List<ElectionData> {
        return try {
            electionRef.get().await()
                .documents
                .mapNotNull { doc ->
                    try {
                        doc.toObject(ElectionData::class.java)?.copy(id = doc.id)
                    } catch (e: Exception) {
                        Log.e("ElectionFetch", "Failed to deserialize election ${doc.id}", e)
                        null
                    }
                }
                .sortedBy { it.title.lowercase() } // sort alphabetically, case insensitive
        } catch (e: Exception) {
            Log.e("ElectionFetch", "Error fetching elections", e)
            emptyList()
        }
    }

    suspend fun getRandomFiveElections(): List<ElectionData> {
        return try {
            electionRef.get().await()
                .documents
                .mapNotNull { doc ->
                    try {
                        doc.toObject(ElectionData::class.java)?.copy(id = doc.id)
                    } catch (e: Exception) {
                        Log.e("ElectionFetch", "Failed to deserialize election ${doc.id}", e)
                        null
                    }
                }
                .shuffled()
                .take(5)
        } catch (e: Exception) {
            Log.e("ElectionFetch", "Error fetching elections", e)
            emptyList()
        }
    }

    suspend fun addRegisteredAddressToElection(electionId: String, newAddress: String): Boolean {
        val electionRef = electionRef.document(electionId)

        return try {
            electionRef.update("registeredAddresses", FieldValue.arrayUnion(newAddress)).await()
            Log.d("Firestore", "Address added successfully to registeredAddresses")
            true
        } catch (e: Exception) {
            Log.e("Firestore", "Failed to add address", e)
            false
        }
    }

}