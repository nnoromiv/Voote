package com.example.voote.firebase.auth

import android.net.Uri
import android.util.Log
import com.example.voote.firebase.data.AppResult
import com.example.voote.model.data.DriverLicenceExtractedData
import com.example.voote.model.data.PassportExtractedData
import com.example.voote.model.data.WalletIdData
import com.example.voote.utils.helpers.generateHMAC
import com.example.voote.utils.helpers.getOrCreateHMACKey
import com.example.voote.utils.helpers.verifyHMAC
import com.example.voote.viewModel.AuthViewModel
import com.example.voote.viewModel.WalletViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class Verification(authManager: AuthViewModel) {

    val db = Firebase.firestore
    val storageRef = FirebaseStorage.getInstance().reference

    val uid = authManager.userUid().toString()
    val userRef = db.collection("users").document(uid)
    private fun kycRef(walletId: String) = userRef.collection("kyc").document(walletId)

    suspend fun checkOrSaveWalletId(walletId: String?, walletViewModel: WalletViewModel) : AppResult<WalletIdData> {

        val walletData = walletViewModel.walletData.value ?: return AppResult.Error("No wallet loaded")
        val address = walletData.address
        val key = getOrCreateHMACKey()

        return try {
            val computedId = generateHMAC(uid + address, key)
            if(walletId.isNullOrEmpty()) {
                createKycDocument(computedId)
                userRef.update("walletId", computedId).await()
                return AppResult.Success("Wallet ID saved",  WalletIdData(computedId))
            }

            return if (!verifyHMAC(uid + address, walletId, key)) {
                renameDocument(walletId, computedId)
                AppResult.Success("Wallet ID renamed", WalletIdData(computedId))
            } else {
                AppResult.Success("Wallet still valid", WalletIdData(walletId))
            }
        } catch (e: Exception) {
            Log.e("WalletID", "Error saving/checking wallet ID", e)
            AppResult.Error("Exception: ${e.localizedMessage}")
        }
    }

    suspend fun createKycDocument(walletId: String) {
        try {
            val ref = kycRef(walletId)
            val hasRef = ref.get().await()

            if (hasRef.exists()) return

            val kycMap = hashMapOf(
                "passportNumber" to "",
                "passportExpiryDate" to "",
                "passportImage" to "",
                "driverLicenceNumber" to "",
                "driverLicenceExpiryDate" to "",
                "driverLicenceImage" to "",
                "residentialAddress" to "",
                "faceImage" to "",
                "lastUpdated" to System.currentTimeMillis()
            )

            ref.set(kycMap).await()
        } catch (e: Exception) {
            Log.e("KYC", "Failed to create KYC document", e)
            throw e
        }
    }

    suspend fun uploadImage(imageUri: Uri, fileName: String): AppResult<Any> {
        return try {
            val imageRef = storageRef.child("images/$uid/$fileName.jpg")

            // Upload file
            imageRef.putFile(imageUri).await()

            // Get download URL
            val downloadUrl = imageRef.downloadUrl.await()

            val ref = userRef.get().await()

            if(!ref.exists()) {
                return AppResult.Error("User does not exist")
            }

            val existingWalletId = ref.getString("walletId")

            if(existingWalletId.isNullOrBlank()) {
                return AppResult.Error("Wallet ID does not exist")
            }

                // Save URL to Firestore
            val kycRef = kycRef(existingWalletId)
            kycRef.update(fileName, downloadUrl.toString()).await()

            AppResult.Success("Image uploaded successfully")
        } catch (e: Exception) {
            Log.e("UploadImage", "Error uploading image", e)
            AppResult.Error("Failed to upload image: ${e.localizedMessage}")
        }
    }

    suspend fun saveDocumentNumbers(passportNumber: String?, passportExpiryDate: String?, driverLicenceNumber: String?, driverLicenceExpiryDate: String?) : AppResult<Any> {
        return try {
            val hasRef = userRef.get().await()
            if (!hasRef.exists()) return AppResult.Error("User does not exist")

            val walletId = hasRef.getString("walletId")
            if (walletId.isNullOrBlank()) return AppResult.Error("Wallet ID does not exist")

            val kycRef = kycRef(walletId)
            val updates = mutableMapOf<String, Any>()
            passportNumber?.takeIf { it.isNotBlank() }?.let { updates["passportNumber"] = it }
            passportExpiryDate?.takeIf { it.isNotBlank() }?.let { updates["passportExpiryDate"] = it }
            driverLicenceNumber?.takeIf { it.isNotBlank() }?.let { updates["driverLicenceNumber"] = it }
            driverLicenceExpiryDate?.takeIf { it.isNotBlank() }?.let { updates["driverLicenceExpiryDate"] = it }

            if (updates.isEmpty()) return AppResult.Error("No valid fields to update")

            kycRef.update(updates).await()
            AppResult.Success("Document numbers saved")
        } catch (e: Exception) {
            Log.e("SaveDocNumber", "Failed to update document numbers", e)
            AppResult.Error("Failed to update documents: ${e.localizedMessage}")
        }
    }

    suspend fun renameDocument( oldDocId: String, newDocId: String ) {
        val oldDocRef = kycRef(oldDocId)
        val newDocRef = kycRef(newDocId)

        try {
            val documentSnapshot = oldDocRef.get().await()
            if (!documentSnapshot.exists()) {
                throw Exception("Old document does not exist")
            }

            val data = documentSnapshot.data
                ?: throw Exception("Old document has no data")

            db.runBatch { batch ->
                batch.set(newDocRef, data)
                batch.delete(oldDocRef)
                batch.update(userRef, "walletId", newDocId)
            }.await()

            Log.d("Firestore", "Document renamed and walletId updated successfully")
        } catch (e: Exception) {
            Log.e("Firestore", "Error renaming document", e)
            throw e // or handle error accordingly
        }
    }

    suspend fun setResidentialAddress(address: String, walletId: String): AppResult<Any> {
        return try {
            kycRef(walletId)
                .update("residentialAddress", address).await()
            AppResult.Success("Residential address updated")
        } catch (e: Exception) {
            Log.e("Firestore", "Failed to update address", e)
            AppResult.Error("Failed to update address: ${e.localizedMessage}")
        }
    }

    suspend fun uploadPassportDataToFirebase(data: PassportExtractedData) : AppResult<Any> {

        if (uid.isEmpty()) {
            return AppResult.Error("User ID is empty")
        }

        val docRef = db.collection("users")
            .document(uid)
            .collection("data")
            .document("passport") // fixed doc name (or use passport number)

        val dataMap = mapOf(
            "countryCode" to data.countryCode,
            "lastName" to data.lastName,
            "givenNames" to data.givenNames,
            "passportNumber" to data.passportNumber,
            "nationality" to data.nationality,
            "dateOfBirth" to data.dateOfBirth,
            "sex" to data.sex,
            "expiryDate" to data.expiryDate,
            "timestamp" to System.currentTimeMillis()
        )

        return try {
            docRef.set(dataMap).await()
            AppResult.Success("Passport data uploaded successfully")
        } catch (e: Exception) {
            Log.e("Firestore", "Failed to upload passport data", e)
            AppResult.Error(e.message ?: "Failed to upload passport data")
        }
    }

    suspend fun uploadDriverLicenceDataToFirebase(data: DriverLicenceExtractedData) : AppResult<Any> {

        if (uid.isEmpty()) {
            return AppResult.Error("User ID is empty")
        }

        val docRef = db.collection("users")
            .document(uid)
            .collection("data")
            .document("driverLicence") // fixed doc name (or use passport number)

        val dataMap = mapOf(
            "surname" to data.surname,
            "firstname" to data.firstname,
            "dob" to data.dob,
            "issueDate" to data.issueDate,
            "licenceNumber" to data.licenceNumber,
            "expiryDate" to data.expiryDate,
            "timestamp" to System.currentTimeMillis()
        )

        return try {
            docRef.set(dataMap).await()
            AppResult.Success("Data uploaded successfully")
        } catch (e: Exception) {
            Log.e("Firestore", "Failed to upload driverLicence data", e)
            AppResult.Error(e.message ?: "Failed to upload driverLicence data")
        }
    }

}