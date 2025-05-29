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
import com.example.voote.navigation.Routes

sealed class BottomNavigationItems(
    val icon: ImageVector,
    val selectedIcon: ImageVector,
    val label: String,
    val route: String
){

    data object Home : BottomNavigationItems(
        icon = Icons.Outlined.Home,
        selectedIcon = Icons.Rounded.Home,
        label = "Home",
        route = Routes.HOME
    )

    data object Elections : BottomNavigationItems(
        icon = Icons.Outlined.HowToVote,
        selectedIcon = Icons.Rounded.HowToVote,
        label = "Elections",
        route = Routes.ELECTIONS
    )

    data object Scan : BottomNavigationItems(
        icon = Icons.Outlined.QrCodeScanner,
        selectedIcon = Icons.Rounded.QrCodeScanner,
        label = "Scan",
        route = Routes.SCAN
    )

    data object History : BottomNavigationItems(
        icon = Icons.Outlined.History,
        selectedIcon = Icons.Rounded.History,
        label = "History",
        route = Routes.HISTORY
    )

    data object Profile : BottomNavigationItems(
        icon = Icons.Outlined.Person,
        selectedIcon = Icons.Rounded.Person,
        label = "Profile",
        route = Routes.PROFILE
    )

    companion object {
        val items = listOf(
            Home,
            Elections,
            Scan,
            History,
            Profile
        )
    }
}
