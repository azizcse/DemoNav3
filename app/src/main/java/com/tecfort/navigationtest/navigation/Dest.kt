package com.tecfort.navigationtest.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/**
 * Nav 3 back-stack keys for this app.
 */
sealed interface Dest : NavKey {

    @Serializable
    data object Splash : Dest

    @Serializable
    data object List : Dest

    @Serializable
    data class Details(val itemId: Int, val title: String) : Dest
}
