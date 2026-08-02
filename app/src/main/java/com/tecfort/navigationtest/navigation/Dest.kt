package com.tecfort.navigationtest.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/**
 * A [NavKey] that takes part in a shared "flow scope": all entries whose key reports the same
 * [scopeId] share one [androidx.lifecycle.ViewModelStore], cleared once no entry with that
 * [scopeId] remains on the back stack. See [flowScopedEntry] and
 * [rememberFlowScopedViewModelStoreNavEntryDecorator].
 */
interface FlowScopedNavKey : NavKey {
    val scopeId: String
}

/**
 * Nav 3 back-stack keys for this app.
 */
sealed interface Dest : NavKey {

    @Serializable
    data object Splash : Dest

    @Serializable
    data object Home : Dest

    @Serializable
    data object Favorite : Dest

    @Serializable
    data object Setting : Dest

    @Serializable
    data object Help : Dest

    @Serializable
    data object About : Dest

    /**
     * @param currentDisplayName the value Setting currently holds, shown pre-filled when this
     *   screen opens - demonstrates passing a value forward, mirroring the value passed back.
     */
    @Serializable
    data class ProfileSetting(val currentDisplayName: String) : Dest

    @Serializable
    data class Details(val itemId: Int, val title: String) : Dest, FlowScopedNavKey {
        override val scopeId: String get() = itemFlowScopeId(itemId)
    }

    @Serializable
    data class Review(val itemId: Int) : Dest, FlowScopedNavKey {
        override val scopeId: String get() = itemFlowScopeId(itemId)
    }
}

internal fun itemFlowScopeId(itemId: Int): String = "item-flow-$itemId"
