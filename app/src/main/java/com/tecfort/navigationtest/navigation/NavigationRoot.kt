package com.tecfort.navigationtest.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import com.tecfort.navigationtest.ui.about.AboutScreen
import com.tecfort.navigationtest.ui.components.AppTopBar
import com.tecfort.navigationtest.ui.details.DetailsScreen
import com.tecfort.navigationtest.ui.favorite.FavoriteScreen
import com.tecfort.navigationtest.ui.help.HelpScreen
import com.tecfort.navigationtest.ui.home.HomeScreen
import com.tecfort.navigationtest.ui.profilesetting.ProfileSettingScreen
import com.tecfort.navigationtest.ui.review.ReviewScreen
import com.tecfort.navigationtest.ui.setting.SettingScreen
import com.tecfort.navigationtest.ui.splash.SplashScreen
import kotlinx.coroutines.launch

private const val SLIDE_DURATION_MS = 300

/**
 * Hosts the app's Nav 3 back stack: splash -> {home, favorite, setting} -> details, plus the
 * profile-setting and account-security ([AccountDest]) screens pushed from the setting tab.
 *
 * The back stack root is always exactly one top-level tab. Tapping a bottom-bar
 * tab collapses the stack down to that tab's root; system back from a non-Home
 * tab root returns to Home, and back from Home exits the app. The bottom bar is
 * shown for top-level tabs only - it is hidden on splash, on details, and on every
 * pushed non-tab screen (profile setting, change/forgot password, OTP).
 *
 * Forward navigation slides the new screen in from the right while the
 * previous screen slides out to the left; back navigation mirrors this,
 * sliding the previous screen back in from the left as the current screen
 * slides out to the right.
 *
 * A navigation drawer (opened via the menu icon shown on top-level tabs) offers
 * the Help and About destinations, both pushed onto the same back stack as any
 * other screen.
 */
@Composable
fun NavigationRoot(
    modifier: Modifier = Modifier,
) {
    val backStack = rememberNavBackStack(Dest.Splash)
    val currentTop = TopLevelDestination.fromKey(backStack.lastOrNull())
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    // Nav 3 has no built-in "pass a result back" channel (unlike Navigation-Compose's
    // SavedStateHandle result API), so the value ProfileSetting hands back to Setting is simply
    // hoisted here, in the nearest composable both entries sit under, and threaded down as a
    // parameter + callback. rememberSaveable keeps it across rotation and process death.
    var profileDisplayName by rememberSaveable { mutableStateOf("") }

    // NavDisplay's own back handler is disabled when the stack has no previous
    // entries (i.e. at a tab root), so this handler is what fires there; once a
    // child like Details is pushed, NavDisplay's handler is enabled and wins.
    BackHandler(enabled = currentTop != null && currentTop != TopLevelDestination.HOME) {
        backStack.replaceRoot(Dest.Home)
    }

    // Takes precedence over the handler above while the drawer is open (most recently
    // registered OnBackPressedCallback wins), so back closes the drawer first.
    BackHandler(enabled = drawerState.isOpen) {
        coroutineScope.launch { drawerState.close() }
    }

    AppNavigationDrawer(
        drawerState = drawerState,
        onHelpClick = {
            coroutineScope.launch { drawerState.close() }
            backStack.add(Dest.Help)
        },
        onAboutClick = {
            coroutineScope.launch { drawerState.close() }
            backStack.add(Dest.About)
        },
    ) {
        Scaffold(
            modifier = modifier,
            topBar = {
                if (currentTop != null) {
                    AppTopBar(
                        title = currentTop.label,
                        onMenuClick = { coroutineScope.launch { drawerState.open() } },
                    )
                }
            },
            bottomBar = {
                AnimatedVisibility(
                    visible = currentTop != null,
                    enter = slideInVertically { it },
                    exit = slideOutVertically { it },
                ) {
                    AppBottomBar(
                        selected = currentTop,
                        onTabClick = { top -> backStack.replaceRoot(top.dest) },
                    )
                }
            },
        ) { innerPadding ->
            NavDisplay(
                modifier = Modifier.padding(innerPadding),
                backStack = backStack,
                onBack = { backStack.removeLastOrNull() },
                entryDecorators = listOf(
                    rememberSaveableStateHolderNavEntryDecorator(),
                    rememberViewModelStoreNavEntryDecorator(),
                    // MUST be last: decorators fold right-to-left, so this one wins
                    // LocalViewModelStoreOwner, and it only overrides entries carrying
                    // FlowScopeIdKey metadata (i.e. those registered via flowScopedEntry).
                    rememberFlowScopedViewModelStoreNavEntryDecorator(backStack),
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
                    splashEntry(onTimeout = { backStack.replaceRoot(Dest.Home) })
                    entry<Dest.Home> {
                        HomeScreen(
                            onItemClick = { item -> backStack.add(Dest.Details(item.id, item.title)) }
                        )
                    }
                    entry<Dest.Favorite> {
                        FavoriteScreen()
                    }
                    entry<Dest.Setting> {
                        SettingScreen(
                            profileDisplayName = profileDisplayName,
                            onEditProfileClick = { backStack.add(Dest.ProfileSetting(profileDisplayName)) },
                        )
                    }
                    entry<Dest.ProfileSetting> { key ->
                        ProfileSettingScreen(
                            initialDisplayName = key.currentDisplayName,
                            onSave = { newDisplayName ->
                                profileDisplayName = newDisplayName
                                backStack.removeLastOrNull()
                            },
                            onChangePasswordClick = { backStack.add(AccountDest.ChangePassword()) },
                            onForgotPasswordClick = { backStack.add(AccountDest.ForgotPassword) },
                            onBackClick = { backStack.removeLastOrNull() },
                        )
                    }
                    accountEntries(backStack)
                    flowScopedEntry<Dest.Details> { key ->
                        DetailsScreen(
                            itemId = key.itemId,
                            title = key.title,
                            onReviewClick = { backStack.add(Dest.Review(key.itemId)) },
                            onBackClick = { backStack.removeLastOrNull() },
                        )
                    }
                    flowScopedEntry<Dest.Review> { key ->
                        ReviewScreen(
                            itemId = key.itemId,
                            onBackClick = { backStack.removeLastOrNull() },
                        )
                    }
                    entry<Dest.Help> {
                        HelpScreen(onBackClick = { backStack.removeLastOrNull() })
                    }
                    entry<Dest.About> {
                        AboutScreen(onBackClick = { backStack.removeLastOrNull() })
                    }
                }
            )
        }
    }
}

/** Collapses the stack to a single top-level [dest]; no-op if already there alone. */
private fun NavBackStack<NavKey>.replaceRoot(dest: Dest) {
    if (size == 1 && lastOrNull() == dest) return
    clear()
    add(dest)
}

/**
 * Registers the splash entry via the [EntryProviderScope] builder, mirroring [flowScopedEntry]:
 * a small named function instead of an inline lambda, so [NavigationRoot]'s entryProvider block
 * stays a flat list of one-liners.
 */
private fun EntryProviderScope<NavKey>.splashEntry(onTimeout: () -> Unit) =
    entry<Dest.Splash> { SplashScreen(onTimeout = onTimeout) }
