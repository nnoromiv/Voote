package com.example.voote.navigation

import kotlinx.serialization.Polymorphic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@Serializable
@Polymorphic
sealed interface AppRoute

@Serializable
object Main

@Serializable
object Login

@Serializable
object Signup

@Serializable
object TokenVerification

@Serializable
object PersonalVerification

@Serializable
object PassportVerification

@Serializable
object DriverLicenceVerification

@Serializable
object AddressVerification

@Serializable
data class FaceVerification(
    val userImageUri: String
)

@Serializable
@SerialName("Home")
data object Home : AppRoute

@Serializable
@SerialName("Elections")
data object Elections : AppRoute

@Serializable
@SerialName("Scan")
data object Scan : AppRoute

@Serializable
@SerialName("History")
data object History : AppRoute

@Serializable
@SerialName("Profile")
data object Profile : AppRoute

@Serializable
object DynamicElectionScreen

@Serializable
data class ScanID(
    val documentType: String
)

@Serializable
object ScanFace

val json = Json {
    serializersModule = SerializersModule {
        polymorphic(AppRoute::class) {
            subclass(Home::class, Home.serializer())
            subclass(Elections::class, Elections.serializer())
            subclass(History::class, History.serializer())
            subclass(Scan::class, Scan.serializer())
            subclass(Profile::class, Profile.serializer())
        }
    }
    classDiscriminator = "type"
}

val homeString = json.encodeToString(AppRoute.serializer(), Home) // ✅ SERIALIZE
val homeObject = json.decodeFromString(AppRoute.serializer(), homeString) // ✅ DESERIALIZE

val profileString = json.encodeToString(AppRoute.serializer(), Profile) // ✅ SERIALIZE
val profileObject = json.decodeFromString(AppRoute.serializer(), profileString) // ✅ DESERIALIZE

val scanString = json.encodeToString(AppRoute.serializer(), Scan) // ✅ SERIALIZE
val scanObject = json.decodeFromString(AppRoute.serializer(), scanString) // ✅ DESERIALIZE

val historyString = json.encodeToString(AppRoute.serializer(), History) // ✅ SERIALIZE
val historyObject = json.decodeFromString(AppRoute.serializer(), historyString) // ✅ DESERIALIZE

val electionsString = json.encodeToString(AppRoute.serializer(), Elections) // ✅ SERIALIZE
val electionsObject = json.decodeFromString(AppRoute.serializer(), electionsString) // ✅ DESERIALIZE
