package com.example.voote.firebase.user

import android.util.Log
import com.example.voote.model.data.KycData
import com.example.voote.model.data.UserData
import com.example.voote.viewModel.AuthViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class User {
    val db = Firebase.firestore

    val authViewModel = AuthViewModel()
    val uid = authViewModel.userUid().toString()
    val userRef = db.collection("users").document(uid)

    suspend fun getUser(): UserData? = try {
        val document = userRef.get().await()
        if (document.exists()) {
            UserData(
                document.getLong("createdAt") ?: 0L,
                document.getString("email") ?: "",
                document.getString("firstName") ?: "",
                document.getBoolean("isPhoneVerified") == true,
                document.getString("lastName") ?: "",
                document.getString("phoneNumber") ?: "",
                document.getString("role") ?: "",
                document.getString("uid") ?: "",
                document.getString("walletId") ?: ""
            )
        } else null
    } catch (e: Exception) {
        Log.e("Firestore", "Error getting document", e)
        null
    }

    suspend fun getKycData(): KycData? = try {
        val document = userRef.get().await()
        if (document.exists()) {
            val walletId = document.getString("walletId") ?: return null
            val kycDocument = userRef.collection("kyc").document(walletId.toString()).get().await()
            if (kycDocument.exists()) {
                KycData(
                    kycDocument.getString("driverLicenceExpiryDate") ?: "",
                    kycDocument.getString("driverLicenceImage") ?: "",
                    kycDocument.getString("driverLicenceNumber") ?: "",
                    kycDocument.getString("faceImage") ?: "",
                    kycDocument.getLong("lastUpdated") ?: 0L,
                    kycDocument.getString("passportExpiryDate") ?: "",
                    kycDocument.getString("passportImage") ?: "",
                    kycDocument.getString("passportNumber") ?: "",
                    kycDocument.getString("residentialAddress") ?: ""
                )
            } else null
        } else null
    } catch (e: Exception) {
        Log.e("Firestore", "Error getting document", e)
        null
    }

}
