package com.example.voote.model.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.HowToVote
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.HowToVote
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.QrCodeScanner
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.voote.navigation.AppRoute
import com.example.voote.navigation.RouteElections
import com.example.voote.navigation.RouteHistory
import com.example.voote.navigation.RouteHome
import com.example.voote.navigation.RouteProfile
import com.example.voote.navigation.RouteScan

sealed class BottomNavigationItems(
    val icon: ImageVector,
    val selectedIcon: ImageVector,
    val label: String,
    val route: AppRoute
){

    data object HomeItem : BottomNavigationItems(
        Icons.Outlined.Home,
        Icons.Rounded.Home,
        "Home",
        RouteHome
    )

    data object ElectionsItem : BottomNavigationItems(
        Icons.Outlined.HowToVote,
        Icons.Rounded.HowToVote,
        "Elections",
        RouteElections
    )

    data object ScanItem : BottomNavigationItems(
        Icons.Outlined.QrCodeScanner,
        Icons.Rounded.QrCodeScanner,
        "Scan",
        RouteScan
    )

    data object HistoryItem : BottomNavigationItems(
        Icons.Outlined.History,
        Icons.Rounded.History,
        "History",
        RouteHistory
    )

    data object ProfileItem : BottomNavigationItems(
        Icons.Outlined.Person,
        Icons.Rounded.Person,
        "Profile",
        RouteProfile
    )

    companion object {
        val items = listOf(
            HomeItem,
            ElectionsItem,
            ScanItem,
            HistoryItem,
            ProfileItem
        )
    }
}
