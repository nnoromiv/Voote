package com.example.voote.firebase.auth

import android.app.Activity
import android.util.Log
import com.example.voote.firebase.data.AppResult
import com.example.voote.model.data.LoginData
import com.example.voote.model.data.SignUpData
import com.example.voote.viewModel.TokenVerificationViewModel
import com.google.firebase.Firebase
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class Auth() {
    val db = Firebase.firestore
    val tokenVerificationViewModel = TokenVerificationViewModel()

    val auth = FirebaseAuth.getInstance()

    suspend fun signUp(email: String, firstName: String, lastName: String, phoneNumber: String, password: String) : AppResult<SignUpData> {
        val displayName = "$firstName $lastName"

        try {
            auth.createUserWithEmailAndPassword(email, password).await()
            val user = auth.currentUser ?: return AppResult.Error("User session missing after sign-up")

            val update = UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .build()

            user.updateProfile(update).await()

            saveUser(
                user,
                firstName,
                lastName,
                phoneNumber,
            )

            return AppResult.Success("Success", SignUpData(user))

        } catch (e: FirebaseAuthUserCollisionException) {
            return AppResult.Error("Email already in use: ${e.message}.")
        } catch (e: Exception) {
            Log.e("Auth", "SignUp error", e)
            return AppResult.Error("Sign-up failed: ${e.message}")
        } catch (e: FirebaseException) {
            Log.e("Auth", "SignUp error: ${e.message}", e)
            return AppResult.Error("Failed: ${e.message}")
        }
    }

    suspend fun saveUser(user: FirebaseUser, firstName: String, lastName: String, phoneNumber: String) : AppResult<Any> {
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

        return try {
            db.collection("users").document(user.uid).set(userMap).await()
            AppResult.Success("User saved successfully.")
        } catch (e: Exception) {
            Log.e("Firestore", "Error saving user", e)
            AppResult.Error(e.message ?: "Unknown error while saving user.")
        }
    }

    suspend fun sendCode(activity: Activity, phoneNumber: String, resendingToken: Boolean = false ): Result<AppResult<Any>> = suspendCoroutine { continuation ->
        val builder = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(30L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    continuation.resume(Result.success(
                        AppResult.Success("Auto verified")
                    ))
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    Log.e("Auth", "Verification failed", e)
                    continuation.resume(Result.failure(e))
                }

                override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                    continuation.resume(Result.success(
                        AppResult.Success("Code sent", verificationId)
                    ))
                }
            })

        val existingToken = tokenVerificationViewModel.resendToken.value
        if (resendingToken && existingToken != null) {
            builder.setForceResendingToken(existingToken)
        }

        try {
            PhoneAuthProvider.verifyPhoneNumber(builder.build())
        } catch (e: Exception) {
            Log.e("Auth", "Error sending code", e)
            continuation.resume(Result.failure(e))
        }
    }

    suspend fun verifyPhoneNumber(verificationId: String, code: String): AppResult<Any> {
        val credential = PhoneAuthProvider.getCredential(verificationId, code)
        val currentUser = auth.currentUser

        if (currentUser == null) {
            return AppResult.Error("Not logged in")
        }

        return try {
            val result = currentUser.linkWithCredential(credential).await()
            val user = result.user ?: return AppResult.Error("Not linked to a user")

            val userDocRef = db.collection("users").document(user.uid)

            db.runTransaction { transaction ->
                transaction.get(userDocRef)
                transaction.update(userDocRef, "isPhoneVerified", true)
            }.await()

            AppResult.Success("Phone number verified")
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            return AppResult.Error("Invalid code: ${e.message}.")
        } catch (e: FirebaseAuthUserCollisionException) {
            return AppResult.Error("This phone number already linked: ${e.message}.")
        } catch (e: Exception) {
            return AppResult.Error("Verification failed: ${e.message}")
        }

    }

    suspend fun logIn(email: String, password: String) : AppResult<LoginData> {
        try {
            auth.signInWithEmailAndPassword(email, password).await()
            val user = auth.currentUser ?: return AppResult.Error("User session missing after login")

            return AppResult.Success("Success", LoginData(user.uid))
        } catch (e: Exception) {
            Log.e("Auth", "Login error", e)
            return AppResult.Error("Failed: ${e.message}")
        }
    }

}