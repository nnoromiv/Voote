package com.example.voote.firebase.data

import androidx.annotation.Keep
import java.math.BigInteger

sealed class AppResult<T>(
    val status: Status,
    val message: String,
    val data: T? = null
) {
    class Success<T>(message: String = "", data: T? = null) : AppResult<T>(Status.SUCCESS, message, data)
    class Error<T>(message: String) : AppResult<T>(Status.ERROR, message)
//    class Pending<T>(message: String = "") : AppResult<T>(Status.PENDING, message)
//    class Continue<T>(message: String = "", data: T?) : AppResult<T>(Status.CONTINUE, message, data)
}

@Keep
enum class Status {
    SUCCESS,
//    PENDING,
//    CONTINUE,
    ERROR
}

@Keep
enum class TAG {
   NONE,
   ELECTION,
   VOTE
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
