package com.tecfort.navigationtest.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
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

private const val SLIDE_DURATION_MS = 300

/**
 * Hosts the app's Nav 3 back stack: splash -> list -> details.
 *
 * Forward navigation slides the new screen in from the right while the
 * previous screen slides out to the left; back navigation mirrors this,
 * sliding the previous screen back in from the left as the current screen
 * slides out to the right.
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
        transitionSpec = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(SLIDE_DURATION_MS),
            ) togetherWith slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(SLIDE_DURATION_MS),
            )
        },
        popTransitionSpec = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(SLIDE_DURATION_MS),
            ) togetherWith slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(SLIDE_DURATION_MS),
            )
        },
        predictivePopTransitionSpec = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(SLIDE_DURATION_MS),
            ) togetherWith slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(SLIDE_DURATION_MS),
            )
        },
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
