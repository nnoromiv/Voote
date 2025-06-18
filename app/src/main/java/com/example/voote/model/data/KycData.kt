package com.example.voote.model.data

data class KycData(
    val driverLicenceExpiryDate: String = "",
    val driverLicenceImage: String = "",
    val driverLicenceNumber: String = "",
    val faceImage: String = "",
    val lastUpdated: Long = 0L,
    val passportExpiryDate: String = "",
    val passportImage: String = "",
    val passportNumber: String = "",
    val residentialAddress: String = ""
) {
    fun calculateEmptinessPercentage(): Int {
        val totalFields = 9
        var emptyCount = 0

        if (driverLicenceExpiryDate.isBlank()) emptyCount++
        if (driverLicenceImage.isBlank()) emptyCount++
        if (driverLicenceNumber.isBlank()) emptyCount++
        if (faceImage.isBlank()) emptyCount++
        if (lastUpdated == 0L) emptyCount++
        if (passportExpiryDate.isBlank()) emptyCount++
        if (passportImage.isBlank()) emptyCount++
        if (passportNumber.isBlank()) emptyCount++
        if (residentialAddress.isBlank()) emptyCount++

        return 100 - (emptyCount * 100) / totalFields
    }
}

