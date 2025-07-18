package com.example.voote.firebase.user

import android.net.Uri
import android.util.Log
import com.example.voote.firebase.data.AppResult
import com.example.voote.firebase.data.TAG
import com.example.voote.model.data.ElectionData
import com.example.voote.model.data.KycData
import com.example.voote.model.data.UserData
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class User(uid: String) {
    val db = Firebase.firestore
    val userId = uid

    val userRef = db.collection("users").document(uid)
    val electionRef = db.collection("elections")
    val candidateRef = db.collection("candidates")
    val storageRef = FirebaseStorage.getInstance().reference

    suspend fun getUser(): UserData? {
        return try {
            val document = userRef.get().await()
            if (!document.exists()) {
                Log.w("Firestore", "User document does not exist")
                return null
            }

            UserData(
                createdAt = document.getLong("createdAt") ?: 0L,
                email = document.getString("email").orEmpty(),
                firstName = document.getString("firstName").orEmpty(),
                isPhoneVerified = document.getBoolean("isPhoneVerified") == true,
                lastName = document.getString("lastName").orEmpty(),
                phoneNumber = document.getString("phoneNumber").orEmpty(),
                role = document.getString("role").orEmpty(),
                uid = document.getString("uid").orEmpty(),
                walletId = document.getString("walletId").orEmpty()
            )
        } catch (e: Exception) {
            Log.e("Firestore", "Failed to retrieve user", e)
            null
        }
    }

    suspend fun getKycData(): KycData? {
        return try {
            val userSnapshot = userRef.get().await()
            if (!userSnapshot.exists()) {
                Log.w("Firestore", "User document not found")
                return null
            }

            val walletId = userSnapshot.getString("walletId")?.takeIf { it.isNotBlank() } ?: return null
            val kycSnapshot = userRef.collection("kyc").document(walletId).get().await()

            if (!kycSnapshot.exists()) {
                Log.w("Firestore", "KYC document not found for walletId: $walletId")
                return null
            }

            return KycData(
                driverLicenceExpiryDate = kycSnapshot.getString("driverLicenceExpiryDate").orEmpty(),
                driverLicenceImage = kycSnapshot.getString("driverLicenceImage").orEmpty(),
                driverLicenceNumber = kycSnapshot.getString("driverLicenceNumber").orEmpty(),
                faceImage = kycSnapshot.getString("faceImage").orEmpty(),
                lastUpdated = kycSnapshot.getLong("lastUpdated") ?: 0L,
                passportExpiryDate = kycSnapshot.getString("passportExpiryDate").orEmpty(),
                passportImage = kycSnapshot.getString("passportImage").orEmpty(),
                passportNumber = kycSnapshot.getString("passportNumber").orEmpty(),
                residentialAddress = kycSnapshot.getString("residentialAddress").orEmpty()
            )
        } catch (e: Exception) {
            Log.e("Firestore", "Error fetching KYC data", e)
            null
        }
    }

    suspend fun addElectionCreatedToDB(electionId: Int, electionTitle: String, startTime: Long, endTime: Long, contractAddress: String, transactionHash: String): Boolean {
        return try {
            val electionData = mapOf(
                "electionId" to electionId,
                "title" to electionTitle,
                "startTime" to startTime,
                "endTime" to endTime,
                "contractAddress" to contractAddress,
                "transactionHash" to transactionHash,
                "createdBy" to userId,
                "createdAt" to System.currentTimeMillis(),
                "tag" to TAG.ELECTION,
                "registeredAddresses" to listOf<String>(),
            )

            // Store under "elections" collection with autogenerated ID
            electionRef
                .add(electionData)
                .await()

            true
        } catch (e: Exception) {
            Log.e("Firestore", "Error saving election: ${e.localizedMessage}", e)
            false
        }
    }

    suspend fun getUserElections(onlyActiveElections: Boolean = false): List<ElectionData> {
        return try {
            val snapshot = electionRef
                .whereEqualTo("createdBy", userId)
                .get()
                .await()

            if (snapshot.isEmpty) {
                emptyList()
            } else {
                val today = System.currentTimeMillis()
                val doc = snapshot.documents
                    .mapNotNull { doc ->
                    doc.toObject(ElectionData::class.java)?.copy(id = doc.id)
                }.sortedBy { it.title.lowercase() }

                if(onlyActiveElections) {
                    doc.filter { it.endTime > today }
                } else {
                    doc
                }
            }
        } catch (e: Exception) {
            Log.e("Firestore", "Failed to fetch user elections", e)
            emptyList()
        }
    }

    suspend fun addCandidateToElection(id: String, electionId: Int, candidateName: String, candidateAddress: String, candidateImageUrl: String, candidateBiographyLink: String, candidateCampaignLink: String, transactionHash: String, candidateId: Int): String {
        try {
            val candidateData = mapOf(
                "id" to id,
                "electionId" to electionId,
                "candidateName" to candidateName,
                "candidateAddress" to candidateAddress,
                "candidateImageUrl" to candidateImageUrl,
                "candidateBiographyLink" to candidateBiographyLink,
                "candidateCampaignLink" to candidateCampaignLink,
                "candidateId" to candidateId,
                "transactionHash" to transactionHash,
                "addedBy" to userId,
                "addedAt" to System.currentTimeMillis(),
            )

            candidateRef
                .add(candidateData)
                .await()

            return id
        } catch (e: Exception) {
            Log.e("Firestore", "Error saving election: ${e.localizedMessage}", e)
            return ""
        }
    }

    suspend fun uploadCandidateImage(id: String, imageUri: Uri, fileName: String): AppResult<Any> {
        return try {
            val imageRef = storageRef.child("candidates/$id/$fileName.jpg")

            // Upload file
            imageRef.putFile(imageUri).await()

            // Get download URL
            val downloadUrl = imageRef.downloadUrl.await()

            val ref = candidateRef
                .whereEqualTo("id", id).get().await()

            if(ref.isEmpty) {
                return AppResult.Error("User does not exist")
            }

            val candidateId = ref.documents[0].id

            // Save URL to Firestore
            candidateRef.document(candidateId).update("candidateImageUrl", downloadUrl.toString()).await()

            AppResult.Success("Image uploaded successfully")
        } catch (e: Exception) {
            Log.e("UploadImage", "Error uploading image", e)
            AppResult.Error("Failed to upload image: ${e.localizedMessage}")
        }
    }

}
