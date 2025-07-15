package com.example.voote.wallet

import android.content.Context
import org.web3j.crypto.Credentials
import org.web3j.crypto.WalletUtils
import java.io.File
import com.example.voote.model.data.WalletData
import com.example.voote.utils.helpers.encryptWithKeyStore
import com.google.gson.Gson
import java.time.Instant

class WalletManager(context: Context) {

    private val walletDirectoryPath = context.filesDir.absolutePath + "/wallets"
    private val generalPassword = "NOGREATPASSWORDUSEDSOVULNERABLE"

    init {
        val walletDirectory = File(walletDirectoryPath)
        if (!walletDirectory.exists()) {
            walletDirectory.mkdirs()
        }
    }

    val walletFileName = "wallet.json"

    /**
     * Generate a new BIP39 wallet for the user
     * @param password - Used to encrypt the wallet
     * @return - Pair of wallet address and mnemonic
     */

    fun createWallet(password: String = generalPassword): WalletData? {
        val wallet = WalletUtils.generateBip39Wallet(password, File(walletDirectoryPath))
        val credentials = WalletUtils.loadBip39Credentials(password, wallet.mnemonic)

        val address = credentials.address
        val mnemonic = wallet.mnemonic
        val privateKey = credentials.ecKeyPair.privateKey.toString(16)

        // Original generated wallet file path
        val originalFile = File(walletDirectoryPath, wallet.filename)
        if(originalFile.exists()) originalFile.delete()
        // Your desired wallet file name
        val walletFile = File(walletDirectoryPath, walletFileName)

        // Rename or move the file to your custom name (overwrite if exists)
        if (walletFile.exists()) walletFile.delete()

        val walletData = saveWalletData(walletFile, address, mnemonic, privateKey)

        return walletData
    }

    /**
     * Import a BIP39 wallet for the user
     * @param mnemonic - The wallet's mnemonic
     * @param password - Used to encrypt the wallet
     * @return - Wallet address
     */

    fun importWallet(mnemonic: String, password: String = generalPassword): WalletData? {
        val credentials = WalletUtils.loadBip39Credentials(password, mnemonic)
        val wallet =WalletUtils.generateBip39WalletFromMnemonic(password, mnemonic, File(walletDirectoryPath))

        val address = credentials.address
        val privateKey = credentials.ecKeyPair.privateKey.toString(16)

        // Original generated wallet file path
        val originalFile = File(walletDirectoryPath, wallet.filename)
        if(originalFile.exists()) originalFile.delete()
        // Your desired wallet file name
        val walletFile = File(walletDirectoryPath, walletFileName)

        // Rename or move the file to your custom name (overwrite if exists)
        if (walletFile.exists()) walletFile.delete()

        val walletData = saveWalletData(walletFile, address, mnemonic, privateKey)
        return walletData
    }

    /**
     * Load an existing wallet for the user
     * @param password - Used to encrypt the wallet
     * @param mnemonic - The wallet's mnemonic
     * @return - Wallet credentials
     */

    fun loadCredentials(password: String = generalPassword, mnemonic: String): Credentials {
        return WalletUtils.loadBip39Credentials(password, mnemonic)
    }

    /**
     * Save wallet data to a file
     * @param file - The file to save the wallet data to
     * @param address - The wallet's address
     * @param mnemonic - The wallet's mnemonic
     * @param privateKey - The wallet's private key
     * @return - Unit
     */

    fun saveWalletData(file: File, address: String, mnemonic: String, privateKey: String) : WalletData? {
        val walletData = WalletData(
            address = address,
            mnemonic = encryptWithKeyStore(mnemonic),
            privateKey =encryptWithKeyStore(privateKey),
            timestamp = Instant.now().toString()
        )
        val gson = Gson()
        val jsonString = gson.toJson(walletData)

        file.writeText(jsonString)
        return walletData
    }

    /**
     * Get wallet data from a file
     * @param walletFileName - The name of the wallet file
     * @return - Wallet data
     */

    fun getWalletContent(walletFileName: String): WalletData? {
        var walletData : WalletData?

        val walletFile = File(walletDirectoryPath, walletFileName)
        if (walletFile.exists()) {
            val content = walletFile.readText()
            if (content.isNotEmpty()) {
                val gson = Gson()
                val data = gson.fromJson(content, WalletData::class.java)
                walletData = data
            } else {
                walletData = null
                println("Wallet file is empty: $walletFileName")
            }
        } else {
            walletData = null
            println("Wallet file does not exist: $walletFileName")
        }

        return walletData
    }

}
