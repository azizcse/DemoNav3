package com.tecfort.navigationtest.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import com.tecfort.navigationtest.ui.details.DetailsScreen
import com.tecfort.navigationtest.ui.list.ListScreen
import com.tecfort.navigationtest.ui.splash.SplashScreen

/**
 * Hosts the app's Nav 3 back stack: splash -> list -> details.
 */
@Composable
fun NavigationRoot(
    modifier: Modifier = Modifier,
) {
    val backStack = rememberNavBackStack(Dest.Splash)
    NavDisplay(
        modifier = modifier,
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        entryProvider = entryProvider {
            entry<Dest.Splash> {
                SplashScreen(
                    onTimeout = {
                        backStack.clear()
                        backStack.add(Dest.List)
                    }
                )
            }
            entry<Dest.List> {
                ListScreen(
                    onItemClick = { item -> backStack.add(Dest.Details(item.id, item.title)) }
                )
            }
            entry<Dest.Details> { key ->
                DetailsScreen(text = key.title)
            }
        }
    )
}
