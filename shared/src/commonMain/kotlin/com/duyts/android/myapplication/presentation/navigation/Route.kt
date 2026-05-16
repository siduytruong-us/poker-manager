package com.duyts.android.myapplication.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Route {
    @Serializable
    data object Splash : Route()

    @Serializable
    data object Login : Route()

    @Serializable
    data class Main(val sessionId: String? = null) : Route()

    @Serializable
    data object PokerSessionList : Route()

    @Serializable
    data object Statistics : Route()

    @Serializable
    data class PokerSessionDetail(val sessionId: String) : Route()

    @Serializable
    data object PokerSessionGraph : Route()

    @Serializable
    data object Settings : Route()

    @Serializable
    data object Profile : Route()

    @Serializable
    data object EditProfile : Route()
}
