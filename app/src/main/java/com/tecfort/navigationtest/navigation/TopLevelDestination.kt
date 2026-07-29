package com.tecfort.navigationtest.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey

/**
 * The tabs shown in the app's bottom navigation bar, in display order.
 */
enum class TopLevelDestination(
    val dest: Dest,
    val label: String,
    val icon: ImageVector,
) {
    HOME(Dest.Home, "Home", Icons.Default.Home),
    FAVORITE(Dest.Favorite, "Favorite", Icons.Default.Favorite),
    SETTING(Dest.Setting, "Setting", Icons.Default.Settings);

    companion object {
        /** Returns the tab that [key] represents, or `null` if it isn't a top-level destination. */
        fun fromKey(key: NavKey?): TopLevelDestination? = entries.find { it.dest == key }
    }
}
