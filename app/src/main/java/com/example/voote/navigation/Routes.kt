package com.example.voote.navigation

import com.example.voote.firebase.data.Status
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
@SerialName("RouteMain")
object RouteMain : AppRoute

@Serializable
@SerialName("RouteLogin")
object RouteLogin : AppRoute

@Serializable
@SerialName("RouteSignUp")
object RouteSignup : AppRoute

@Serializable
@SerialName("RouteTokenVerification")
data class RouteTokenVerification(
    val phoneNumber: String
) : AppRoute

@Serializable
@SerialName("RoutePersonalVerification")
object RoutePersonalVerification : AppRoute

@Serializable
@SerialName("RoutePassportVerification")
object RoutePassportVerification : AppRoute

@Serializable
@SerialName("RouteDriverLicenceVerification")
object RouteDriverLicenceVerification : AppRoute

@Serializable
@SerialName("RouteAddressVerification")
object RouteAddressVerification : AppRoute

@Serializable
@SerialName("RouteFaceVerification")
data class RouteFaceVerification(
    val userImageUri: String
) : AppRoute

@Serializable
@SerialName("RouteHome")
data object RouteHome : AppRoute

@Serializable
@SerialName("RouteElections")
data object RouteElections : AppRoute

@Serializable
@SerialName("RouteScan")
data object RouteScan : AppRoute

@Serializable
@SerialName("RouteHistory")
data object RouteHistory : AppRoute

@Serializable
@SerialName("RouteProfile")
data object RouteProfile : AppRoute

@Serializable
@SerialName("RouteDynamicElection")
data class RouteDynamicElection(
    val id: String
) : AppRoute

@Serializable
@SerialName("RouteScanID")
data class RouteScanID(
    val documentType: String
) : AppRoute

@Serializable
@SerialName("RouteScanFace")
object RouteScanFace : AppRoute

@Serializable
@SerialName("RouteStatus")
data class RouteStatus (
    val status: Status,
    val nextScreen: String? = ""
) : AppRoute

@Serializable
@SerialName("RouteLoader")
data object RouteLoader : AppRoute

@Serializable
@SerialName("RouteImportWallet")
data object RouteImportWallet : AppRoute


val json = Json {
    classDiscriminator = "type"
    serializersModule = SerializersModule {
        polymorphic(AppRoute::class) {
            subclass(RouteMain::class, RouteMain.serializer())
            subclass(RouteLogin::class, RouteLogin.serializer())
            subclass(RouteSignup::class, RouteSignup.serializer())
            subclass(RouteTokenVerification::class, RouteTokenVerification.serializer())
            subclass(RoutePersonalVerification::class, RoutePersonalVerification.serializer())
            subclass(RoutePassportVerification::class, RoutePassportVerification.serializer())
            subclass(RouteDriverLicenceVerification::class, RouteDriverLicenceVerification.serializer())
            subclass(RouteAddressVerification::class, RouteAddressVerification.serializer())
            subclass(RouteFaceVerification::class, RouteFaceVerification.serializer())
            subclass(RouteHome::class, RouteHome.serializer())
            subclass(RouteElections::class, RouteElections.serializer())
            subclass(RouteScan::class, RouteScan.serializer())
            subclass(RouteHistory::class, RouteHistory.serializer())
            subclass(RouteProfile::class, RouteProfile.serializer())
            subclass(RouteDynamicElection::class, RouteDynamicElection.serializer())
            subclass(RouteScanID::class, RouteScanID.serializer())
            subclass(RouteScanFace::class, RouteScanFace.serializer())
            subclass(RouteStatus::class, RouteStatus.serializer())
            subclass(RouteLoader::class, RouteLoader.serializer())
            subclass(RouteImportWallet::class, RouteImportWallet.serializer())
        }
    }
}

fun AppRoute.toJson(): String = json.encodeToString(AppRoute.serializer(), this)
fun String.toAppRoute(): AppRoute = json.decodeFromString(AppRoute.serializer(), this)

