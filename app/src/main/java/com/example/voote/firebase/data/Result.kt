package com.example.voote.firebase.data

import androidx.annotation.Keep
import com.google.firebase.Timestamp
import java.math.BigInteger

sealed class AppResult<T>(
    val status: STATUS,
    val message: String,
    val data: T? = null
) {
    class Success<T>(message: String = "", data: T? = null) : AppResult<T>(STATUS.SUCCESS, message, data)
    class Error<T>(message: String) : AppResult<T>(STATUS.ERROR, message)
//    class Pending<T>(message: String = "") : AppResult<T>(Status.PENDING, message)
//    class Continue<T>(message: String = "", data: T?) : AppResult<T>(Status.CONTINUE, message, data)
}

@Keep
enum class STATUS {
    SUCCESS,
//    PENDING,
//    CONTINUE,
    ERROR
}

@Keep
enum class TAG {
   NONE,
   ELECTION,
}

@Keep
enum class AUDIT {
    CREATE_ELECTION,
    CREATE_CANDIDATE,
    VOTE,
    LOGIN,
    KYC,
    NONE
}

data class VotePreview(
    val gasPrice: BigInteger?,
    val estimateGas: BigInteger?,
    val nonce: BigInteger?,
    val errorMessage: String? = null
)

data class BreakdownResult(
    val candidatesName: MutableList<String?>? = null,
    val startTime: BigInteger? = null,
    val endTime: BigInteger? = null,
    val voteCounts: MutableList<BigInteger?>? = null,
    val percentages: MutableList<BigInteger?>? = null,
)

data class WinnerResult(
    val candidatesName: MutableList<String?>? = null,
    val candidatesAddress: MutableList<String?>? = null,
    val voteCount: BigInteger? = null,
)

data class AuditLogEntry(
    val action: AUDIT = AUDIT.NONE,
    val status: STATUS = STATUS.ERROR,
    val details: String? = null,
    val timestamp: Timestamp? = null
)
