package com.example.voote.firebase.auth

import android.net.Uri
import android.util.Log
import com.example.voote.viewModel.AuthViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class Verification {
    val db = Firebase.firestore
    val storageRef = FirebaseStorage.getInstance().reference

    val authViewModel = AuthViewModel()
    val uid = authViewModel.userUid().toString()
    val userRef = db.collection("users").document(uid)

    private val tag = "Verification"

    fun saveWalletToDocument(walletId: String, onContinue: (String) -> Unit) {
        userRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val existingWalletId = document.getString("walletId")
                    if (existingWalletId.isNullOrBlank()) {
                        // walletId not set, so update it
                        userRef.update("walletId", walletId)
                            .addOnSuccessListener {
                                beginVerification(walletId)
                                Log.d(tag, "Wallet ID updated successfully")
                                onContinue(walletId)
                            }
                            .addOnFailureListener { e ->
                                Log.e(tag, "Error updating wallet ID", e)
                                onContinue(walletId) // still return new walletId on failure
                            }
                    } else {
                        // walletId already set, just continue without update
                        Log.d(tag, "Wallet ID already set, skipping update")
                        onContinue(existingWalletId)
                    }
                } else {
                    Log.e(tag, "User document does not exist")
                    onContinue(walletId) // fallback: pass new walletId
                }
            }
            .addOnFailureListener { e ->
                Log.e(tag, "Error fetching user document", e)
                onContinue(walletId) // fallback: pass new walletId
            }
    }

    fun beginVerification(walletId: String) {
        val kycRef = userRef.collection("kyc").document(walletId)

        kycRef.get()
            .addOnSuccessListener { document ->
                if (!document.exists()) {
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

                    kycRef.set(kycMap)
                        .addOnSuccessListener {
                            Log.d(tag, "Firestore write successful")
                        }
                        .addOnFailureListener { e ->
                            Log.e(tag, "Firestore write failed", e)
                        }
                } else {
                    Log.d(tag, "KYC document already exists - skipping write")
                }
            }
            .addOnFailureListener { e ->
                Log.e(tag, "Error checking KYC document existence", e)
            }
    }

    fun uploadImage(imageUri: Uri, fileName: String, imageToSave: String, onError: (Exception) -> Unit) {
        val imageRef = storageRef.child("images/$uid/$fileName.jpg")

        val uploadTask = imageRef.putFile(imageUri)

        uploadTask.addOnSuccessListener {
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                saveImageToDB(uri, imageToSave)
            }
        }.addOnFailureListener { exception ->
            onError(exception)
        }

    }

    fun saveImageToDB(imageUri: Uri, imageToSave: String) {
        userRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val walletId = document.getString("walletId")
                    if (walletId != null) {
                        val kycRef = userRef.collection("kyc").document(walletId)
                        kycRef.update(
                            imageToSave, imageUri.toString(),
                            )
                            .addOnSuccessListener {
                                Log.d(tag, "Image URL updated successfully")
                            }
                            .addOnFailureListener { e ->
                                Log.e(tag, "Error updating image URL", e)
                            }
                    }
                } else {
                    Log.d("Firestore", "Document does not exist")
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error getting document", e)
            }

    }

    fun saveDocumentNumbersToDB(passportNumber: String?, passportExpiryDate: String?, driverLicenceNumber: String?, driverLicenceExpiryDate: String?, onSuccess: () -> Unit ) {

        userRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val walletId = document.getString("walletId")
                    if (walletId != null) {
                        val kycRef = userRef.collection("kyc").document(walletId)

                        // Build a map of non-null and non-blank fields to update
                        val updates = mutableMapOf<String, Any>()
                        if (!passportNumber.isNullOrBlank()) {
                            updates["passportNumber"] = passportNumber
                        }
                        if (!passportExpiryDate.isNullOrBlank()) {
                            updates["passportExpiryDate"] = passportExpiryDate
                        }
                        if (!driverLicenceNumber.isNullOrBlank()) {
                            updates["driverLicenceNumber"] = driverLicenceNumber
                        }
                        if (!driverLicenceExpiryDate.isNullOrBlank()) {
                            updates["driverLicenceExpiryDate"] = driverLicenceExpiryDate
                        }

                        // Only update if there's at least one valid field
                        if (updates.isNotEmpty()) {
                            kycRef.update(updates)
                                .addOnSuccessListener {
                                    onSuccess()
                                    Log.d(tag, "KYC fields updated successfully")
                                }
                                .addOnFailureListener { e ->
                                    Log.e(tag, "Error updating KYC fields", e)
                                }
                        } else {
                            Log.d(tag, "No valid KYC data to update")
                        }
                    } else {
                        Log.e(tag, "walletId is null")
                    }
                } else {
                    Log.d(tag, "User document does not exist")
                }
            }
            .addOnFailureListener { e ->
                Log.e(tag, "Error fetching user document", e)
            }
    }

    suspend fun renameDocument( oldDocId: String, newDocId: String ) {
        val oldDocRef = userRef.collection("kyc").document(oldDocId)
        val newDocRef = userRef.collection("kyc").document(newDocId)

        try {
            val documentSnapshot = oldDocRef.get().await()
            if (!documentSnapshot.exists()) {
                throw Exception("Old document does not exist")
            }

            val data = documentSnapshot.data
                ?: throw Exception("Old document has no data")

            // Copy data to new document
            newDocRef.set(data).await()

            // Delete old document
            oldDocRef.delete().await()

            // Update walletId field in user document
            userRef.update("walletId", newDocId).await()

            Log.d("Firestore", "Document renamed and walletId updated successfully")
        } catch (e: Exception) {
            Log.e("Firestore", "Error renaming document", e)
            throw e // or handle error accordingly
        }
    }

    suspend fun setResidentialAddress(residentialAddress: String, walletId: String): Boolean {
        val kycRef = userRef.collection("kyc").document(walletId)
        return try {
            kycRef.update("residentialAddress", residentialAddress).await()
            true
        } catch (e: Exception) {
            Log.e("Firestore", "Failed to update residential address", e)
            false
        }
    }




}