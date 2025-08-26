package com.example.voote.blockchain

import android.util.Log
import com.example.voote.contract.Voote
import com.example.voote.firebase.data.VotePreview
import com.example.voote.utils.Constants
import com.example.voote.utils.helpers.decryptWithKeyStore
import com.example.voote.viewModel.WalletViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.web3j.abi.EventEncoder
import org.web3j.abi.FunctionEncoder
import org.web3j.abi.TypeReference
import org.web3j.abi.datatypes.Address
import org.web3j.abi.datatypes.Event
import org.web3j.abi.datatypes.Function
import org.web3j.abi.datatypes.Utf8String
import org.web3j.abi.datatypes.generated.Uint16
import org.web3j.abi.datatypes.generated.Uint32
import org.web3j.abi.datatypes.generated.Uint8
import org.web3j.crypto.Credentials
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.core.methods.request.Transaction
import org.web3j.protocol.core.methods.response.TransactionReceipt
import org.web3j.protocol.http.HttpService
import org.web3j.tx.RawTransactionManager
import org.web3j.tx.gas.ContractGasProvider
import org.web3j.tx.gas.DefaultGasProvider
import org.web3j.utils.Convert
import org.web3j.utils.Numeric
import java.math.BigDecimal
import java.math.BigInteger

class Connector(walletViewModel: WalletViewModel)  {

    val userAddress = walletViewModel.walletData.value?.address
    val constant = Constants()
    val web3j: Web3j = Web3j.build(HttpService(constant.rpcUrl))

    suspend fun connectContract(privateKey: String): Voote? = withContext(Dispatchers.IO) {
        try {

            if (privateKey.isEmpty()) {
                Log.wtf("Connector", "Private key is null")
                return@withContext null
            }

            val actualKey = decryptWithKeyStore(privateKey)

            val credential = Credentials.create(actualKey)

            val gasProvider = object : ContractGasProvider {
                override fun getGasPrice(contractFunc: String?): BigInteger {
                    return web3j.ethGasPrice().send().gasPrice
                }

                override fun getGasLimit(contractFunc: String?): BigInteger {
                    return BigInteger.valueOf(300_000)
                }

                override fun getGasPrice(): BigInteger {
                    return getGasPrice(null)
                }

                override fun getGasLimit(): BigInteger {
                    return getGasLimit(null)
                }
            }

            val chainId = BigInteger.valueOf(constant.chainId)

            val txManager = RawTransactionManager(
                web3j,
                credential,
                chainId.toLong()
            )

            val contract = Voote.load(
                constant.vooteContractAddress,
                web3j,
                txManager,
                gasProvider
            )

            contract
        } catch (e: Exception) {
            Log.e("Connector", "Failed to connect to contract", e)
            null
        }
    }

    private suspend fun previewFunctionCall( function: Function, errorContext: String): VotePreview = withContext(Dispatchers.IO) {
        if (userAddress.isNullOrEmpty()) {
            Log.e("SmartContractPreview", "$errorContext: Wallet address is null or empty")
            return@withContext VotePreview(null, null, null, "No wallet address")
        }

        val gasPrice = try {
            web3j.ethGasPrice().send().gasPrice
        } catch (e: Exception) {
            Log.w("SmartContractPreview", "$errorContext: Failed to fetch gas price, using default", e)
            DefaultGasProvider.GAS_PRICE
        }

        val nonce = web3j.ethGetTransactionCount(userAddress, DefaultBlockParameterName.LATEST).send().transactionCount
        val encodedFunction = FunctionEncoder.encode(function)

        val estimateGasResponse = web3j.ethEstimateGas(
            Transaction.createFunctionCallTransaction(
                userAddress,
                nonce,
                gasPrice,
                null,
                constant.vooteContractAddress,
                encodedFunction
            )
        ).send()

        if (estimateGasResponse.hasError()) {
            val err = estimateGasResponse.error.message.replaceFirstChar { char -> char.uppercase() }
            Log.e("SmartContractPreview", "$errorContext: $err")
            return@withContext VotePreview(null, null, null, err)
        }

        val estimateGas = estimateGasResponse.amountUsed

        VotePreview(gasPrice, estimateGas, nonce)

    }

    fun previewCreateElection(electionTitle: String, startTimeMillis: Long, endTimeMillis: Long): VotePreview {

        val startTime = BigInteger.valueOf(startTimeMillis / 1000)
        val endTime = BigInteger.valueOf(endTimeMillis / 1000)

        val function = Function(
            "createElection",
            listOf(Utf8String(electionTitle), Uint32(startTime), Uint32(endTime)),
            emptyList()
        )

        return runBlocking {
            previewFunctionCall(function, "Creating Election")
        }
    }

    fun previewRegisterCandidate(electionId: BigInteger, candidateName: String, candidateAddress: String): VotePreview {

        val function = Function(
            "registerCandidate",
            listOf(Uint16(electionId), Utf8String(candidateName), Address(candidateAddress)),
            emptyList()
        )

        return runBlocking {
            previewFunctionCall(function, "Registering Candidate")
        }
    }

    fun previewVote(electionId: BigInteger, candidateId: BigInteger): VotePreview {
        val function = Function(
            "vote",
            listOf(Uint16(electionId), Uint8(candidateId)),
            emptyList()
        )

        return runBlocking {
            previewFunctionCall(function, "Voting")
        }
    }

    fun createdElectionId(receipt: TransactionReceipt?): Int? {
        if(receipt == null) {
            Log.e("Connector", "Receipt is null")
            return null
        }

        try {
            val event = Event(
                "ElectionCreated",
                listOf(
                    TypeReference.create(Uint16::class.java, true), // indexed electionId
                    TypeReference.create(Utf8String::class.java),     // title
                )
            )

            val logs = receipt.logs
            val eventSignature = EventEncoder.encode(event)

            for (log in logs) {
                if (log.topics.isNotEmpty() && log.topics[0] == eventSignature) {
                    val electionId = Numeric.toBigInt(log.topics[1])
                    Log.d("SmartContract", "Election ID: $electionId")
                    return electionId.toInt()
                }
            }
        } catch (e: Exception) {
            Log.e("Connector", "Failed to log of election created", e)
        }
        return null
    }

    fun createdCandidateId(receipt: TransactionReceipt?): Int? {
        if (receipt == null) {
            Log.e("Connector", "Receipt is null")
            return null
        }

        try {
            val event = Event(
                "CandidateRegistered",
                listOf(
                    TypeReference.create(Uint16::class.java, true),
                    TypeReference.create(Uint8::class.java, true),
                    TypeReference.create(Utf8String::class.java),
                    TypeReference.create(Address::class.java),
                )
            )

            val logs = receipt.logs
            val eventSignature = EventEncoder.encode(event)

            for (log in logs) {
                if (log.topics.isNotEmpty() && log.topics[0] == eventSignature) {
                    val candidateId = Numeric.toBigInt(log.topics[2])
                    Log.d("SmartContract", "Election ID: $candidateId")
                    return candidateId.toInt()
                }
            }
        } catch (e: Exception) {
            Log.e("Connector", "Failed to parse event logs", e)
            return null
        }

        return null
    }

    suspend fun getWalletBalance(address: String): BigDecimal? {
        return withContext(Dispatchers.IO) {
            try {
                val ethBalance = web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST).send()
                val balanceInWei = ethBalance.balance

                Convert.fromWei(balanceInWei.toString(), Convert.Unit.ETHER)
            } catch (e: Exception) {
                Log.e("Connector", "Failed to get wallet balance", e)
                null
            }
        }
    }


}