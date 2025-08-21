package com.example.voote.model.data

data class PassportExtractedData (
    var countryCode: String = "",
    var lastName: String = "",
    var givenNames: String = "",
    var passportNumber: String = "",
    var nationality: String = "",
    var dateOfBirth: String = "",
    var sex: String = "",
    var expiryDate: String = "",
)

data class DriverLicenceExtractedData(
    var surname: String = "",
    var firstname: String = "",
    var dob: String = "",
    var issueDate: String = "",
    var expiryDate: String = "",
    var licenceNumber: String = "",
    var address: String = ""
)

fun DriverLicenceExtractedData.hasAnyField(): Boolean {
    return listOf(
        surname,
        firstname,
        dob,
        issueDate,
        expiryDate,
        licenceNumber,
        address
    ).any { it.isNotBlank() }
}

data class FaceLivelinessResult(
    var smile: String = "",         // "Yes" / "No" / "Unknown"
    var leftEyeOpen: String = "",   // "Yes" / "No" / "Unknown"
    var rightEyeOpen: String = "",  // "Yes" / "No" / "Unknown"
    var headEulerY: Float = 0f,     // Head rotation around Y axis
    var headEulerZ: Float = 0f,     // Head tilt
    var trackingId: Int? = 0,      // ML Kit tracking ID (nullable)
    var isLive: String = ""         // "Yes" / "Uncertain"
)

