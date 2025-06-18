package com.example.voote.firebase.auth

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import androidx.navigation.NavController
import com.example.voote.navigation.PersonalVerification
import com.example.voote.navigation.TokenVerification
import com.example.voote.navigation.homeObject
import com.example.voote.viewModel.TokenVerificationViewModel
import com.google.firebase.Firebase
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.firestore
import java.util.concurrent.TimeUnit

class Auth {
    val db = Firebase.firestore
    val tokenVerificationViewModel = TokenVerificationViewModel()
    val token = tokenVerificationViewModel.resendToken.value

    fun handleSignUp(activity: Activity, auth: FirebaseAuth, email: String, firstName: String, lastName: String, phoneNumber: String, password: String, navController: NavController) {
        val displayName = "$firstName $lastName"

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { task ->
                val user = task.user
                if (user != null) {
                    Log.d("AUTH", "Success: ${user.uid}")

                    // Update display name
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(displayName)
                        .build()

                    user.updateProfile(profileUpdates)
                        .addOnCompleteListener { updateTask ->
                            if (updateTask.isSuccessful) {
                                saveUserToDatabase(
                                    activity,
                                    auth,
                                    user,
                                    firstName,
                                    lastName,
                                    phoneNumber,
                                    navController
                                )
                            } else {
                                Log.e("AUTH", "Failed to update display name", updateTask.exception)
                            }
                        }
                } else {
                    Log.d("AUTH", "User creation failed")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("AUTH", "Failed: ${exception.message}")
            }
    }

    fun saveUserToDatabase(activity: Activity, auth: FirebaseAuth, user: FirebaseUser, firstName: String, lastName: String, phoneNumber: String, navController: NavController) {

        val userMap = hashMapOf(
            "uid" to user.uid,
            "email" to user.email,
            "firstName" to firstName,
            "lastName" to lastName,
            "phoneNumber" to phoneNumber,
            "isPhoneVerified" to false,
            "role" to "user",
            "createdAt" to System.currentTimeMillis()
        )

        db.collection("users").document(user.uid).set(userMap)
            .addOnSuccessListener {
                sendVerificationCode(
                    auth,
                    activity,
                    phoneNumber,
                    navController
                )
            }
            .addOnFailureListener { e ->
                Log.e("AUTH", "Firestore write failed", e)
            }
    }

    @SuppressLint("SuspiciousIndentation")
    fun sendVerificationCode(auth: FirebaseAuth, activity: Activity, phoneNumber: String, navController: NavController, resendingToken: Boolean = false) {

        val builder = PhoneAuthOptions.newBuilder(auth).setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(object:  PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    Log.d("AUTH", "Auto verification completed")
                    // Optionally sign in user automatically here
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    Log.e("AUTH", "Verification failed: ${e.message}")
                    // Notify user of failure
                }

                override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                    super.onCodeSent(verificationId, token)
                    tokenVerificationViewModel.setStoredPhoneNumber(phoneNumber)
                    tokenVerificationViewModel.setStoredVerificationId(verificationId)
                    tokenVerificationViewModel.setResendToken(token)

                    navController.navigate(TokenVerification)

                    Log.d("AUTH", "Code sent: $token")
                    // Save verificationId and token for later use
                }
            })

            if(resendingToken && token != null){
                builder.setForceResendingToken(token)
            }

            val options = builder.build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun verifyPhoneNumberWithCode(auth: FirebaseAuth, verificationId: String, otpCode: String, navController: NavController) {
        val credential = PhoneAuthProvider.getCredential(verificationId, otpCode)
        val currentUser = auth.currentUser

        if (currentUser != null) {
            currentUser.linkWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = task.result?.user
                        val uid = user?.uid

                        if(uid != null){
                            Log.d("AUTH", "User ID: $uid")
                            setIsVerifiedPhone(uid, true)
                        } else {
                            Log.e("AUTH", "User ID is null")
                        }

                        Log.d("AUTH", "Verification successful, UID: $uid")

                        // Navigate to next screen
                        navController.navigate(PersonalVerification)
                    } else {
                        Log.e("AUTH", "Verification failed: ${task.exception?.message}")
                        // Show error to user
                    }
                }
        } else {
            Log.e("AUTH", "Current user is null")
        }
    }

    fun setIsVerifiedPhone(uid: String, isPhoneVerified: Boolean){
        val userDocRef = db.collection("users").document(uid)

        userDocRef.update("isPhoneVerified", isPhoneVerified)
            .addOnSuccessListener {
                Log.d("AUTH", "Phone verification status updated successfully")
            }
            .addOnFailureListener { e ->
                Log.e("AUTH", "Error updating phone verification status", e)
            }
    }

    fun handleLogIn(auth: FirebaseAuth, email: String, password: String, onError: (String) -> Unit = {}, navController: NavController) {

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { task ->
                val user = task.user
                if (user != null) {
                    val uid = user.uid
                    isKycCollectionCreated(uid, navController)
                } else {
                    Log.d("AUTH", "User Sign In failed")
                }
            }
            .addOnFailureListener { exception ->
                onError("Login failed: ${exception.message}")
                Log.e("AUTH", "Failed: ${exception.message}")
            }
    }

    fun isKycCollectionCreated(uid: String, navController: NavController) {
        val kycCollectionRef = db.collection("users").document(uid).collection("kyc")

        kycCollectionRef.get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    Log.d("Firestore", "KYC collection exists and has documents")
                    // You can iterate documents if needed
                    for (doc in querySnapshot.documents) {
                        Log.d("Firestore", "Doc id: ${doc.id}, data: ${doc.data}")
                    }
                    navController.navigate(homeObject)
                } else {
                    Log.d("Firestore", "KYC collection does not exist or is empty")
                    navController.navigate(PersonalVerification)
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error getting KYC collection", exception)
            }

    }

}