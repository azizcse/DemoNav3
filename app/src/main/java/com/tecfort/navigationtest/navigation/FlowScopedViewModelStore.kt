package com.tecfort.navigationtest.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.ViewModelStoreProvider
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.rememberViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.rememberViewModelStoreProvider
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.NavMetadataKey
import androidx.navigation3.runtime.get
import androidx.navigation3.runtime.metadata
import androidx.savedstate.compose.LocalSavedStateRegistryOwner

/**
 * Metadata slot carrying a [FlowScopedNavKey.scopeId] onto its `NavEntry`. `NavEntry.key` is
 * private in navigation3-runtime 1.1.4, so metadata is the only channel a [NavEntryDecorator] has
 * for reading anything key-derived.
 */
object FlowScopeIdKey : NavMetadataKey<String> {
    override fun toString(): String = "com.tecfort.navigationtest.navigation.FlowScopeIdKey"
}

/**
 * Like `entry<K> { ... }`, but attaches the metadata that opts the entry into its shared flow
 * scope. Always use this - instead of a bare `entry<K>` - for a [FlowScopedNavKey]: a missing
 * metadata entry silently degrades to a per-entry ViewModelStore, with no error.
 */
inline fun <reified K : FlowScopedNavKey> EntryProviderScope<NavKey>.flowScopedEntry(
    noinline content: @Composable (K) -> Unit,
) = entry<K>(metadata = { key -> metadata { put(FlowScopeIdKey, key.scopeId) } }, content = content)

/**
 * Provides every `NavEntry` carrying a [FlowScopeIdKey] with a [ViewModelStoreOwner] shared by all
 * entries reporting the same scope id, and leaves every other entry untouched so the outer
 * official `androidx.lifecycle.viewmodel.navigation3.ViewModelStoreNavEntryDecorator` keeps
 * providing its per-entry owner.
 *
 * Must be the LAST decorator in `NavDisplay(entryDecorators = ...)`: decorators are applied with
 * `foldRight`, so the first entry in the list is the outermost wrapper and the last one wins for
 * `LocalViewModelStoreOwner`.
 */
class FlowScopedViewModelStoreNavEntryDecorator(
    provider: ViewModelStoreProvider,
) : NavEntryDecorator<NavKey>(
    // No onPop: it only hands us a contentKey, which cannot be mapped back to a scope id (NavEntry.key
    // is private). Clearing is driven off the back stack instead, in
    // rememberFlowScopedViewModelStoreNavEntryDecorator below.
    decorate = { entry ->
        val scopeId = entry.metadata[FlowScopeIdKey]
        if (scopeId == null) {
            entry.Content()
        } else {
            val owner = rememberViewModelStoreOwner(
                key = scopeId,
                provider = provider,
                savedStateRegistryOwner = LocalSavedStateRegistryOwner.current,
            )
            CompositionLocalProvider(LocalViewModelStoreOwner provides owner) { entry.Content() }
        }
    },
)

private const val FLOW_SCOPE_PROVIDER_KEY = "com.tecfort.navigationtest.flowScopeProvider"

/**
 * Remembers a [FlowScopedViewModelStoreNavEntryDecorator] whose stores survive configuration
 * changes (they live in a ViewModel held by [parentViewModelStoreOwner]'s store) and are cleared
 * as soon as no entry on [backStack] reports their scope id.
 */
@Composable
fun rememberFlowScopedViewModelStoreNavEntryDecorator(
    backStack: List<NavKey>,
    parentViewModelStoreOwner: ViewModelStoreOwner =
        checkNotNull(LocalViewModelStoreOwner.current) {
            "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
        },
): NavEntryDecorator<NavKey> {
    val provider = rememberViewModelStoreProvider(
        key = FLOW_SCOPE_PROVIDER_KEY,
        parent = parentViewModelStoreOwner,
    )

    // The back stack is the single source of truth for which scopes are alive. Reading it here
    // subscribes this composition to it, so the effect below re-runs on every shape change -
    // including a bulk clear() and entries that were never composed.
    val liveScopeIds = backStack.filterIsInstance<FlowScopedNavKey>().mapTo(mutableSetOf()) { it.scopeId }
    val seenScopeIds = remember(provider) { mutableSetOf<String>() }
    DisposableEffect(provider, liveScopeIds) {
        // clearKey is reference-counted and a no-op for unknown keys, so this is always safe: a
        // store still composed in an exit animation is only marked disposable, and is actually
        // removed only when its last reference token closes.
        (seenScopeIds - liveScopeIds).forEach(provider::clearKey)
        seenScopeIds.clear()
        seenScopeIds.addAll(liveScopeIds)
        onDispose {}
    }

    return remember(provider) { FlowScopedViewModelStoreNavEntryDecorator(provider) }
}
