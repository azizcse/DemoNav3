package com.tecfort.navigationtest.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.tecfort.navigationtest.ui.account.ChangePasswordScreen
import com.tecfort.navigationtest.ui.account.ForgotPasswordScreen
import com.tecfort.navigationtest.ui.account.OtpScreen

/**
 * Registers the whole account-security route group ([AccountDest]) via the
 * [EntryProviderScope] builder, mirroring [splashEntry]: a small named function instead of three
 * inline lambdas, so [NavigationRoot]'s entryProvider block stays a flat list of one-liners.
 *
 * Every member is registered with [flowScopedEntry] rather than a bare `entry<K>` - required so
 * they share one `AccountSecurityViewModel` instance for the lifetime of the flow.
 */
internal fun EntryProviderScope<NavKey>.accountEntries(backStack: NavBackStack<NavKey>) {
    flowScopedEntry<AccountDest.ChangePassword> { key ->
        ChangePasswordScreen(
            isReset = key.isReset,
            onForgotPasswordClick = { backStack.add(AccountDest.ForgotPassword) },
            onChanged = { backStack.exitAccountFlow() },
            onBackClick = { backStack.removeLastOrNull() },
        )
    }
    flowScopedEntry<AccountDest.ForgotPassword> {
        ForgotPasswordScreen(
            onCodeSent = { email -> backStack.add(AccountDest.Otp(email)) },
            onBackClick = { backStack.removeLastOrNull() },
        )
    }
    flowScopedEntry<AccountDest.Otp> { key ->
        OtpScreen(
            email = key.email,
            onVerified = { backStack.add(AccountDest.ChangePassword(isReset = true)) },
            onBackClick = { backStack.removeLastOrNull() },
        )
    }
}

/**
 * Pops every trailing account-flow entry at once, returning to whatever pushed the flow (Profile
 * Setting). A single `removeLastOrNull()` would only pop one screen - e.g. landing back on Otp
 * once the reset path's ChangePassword screen finishes, instead of leaving the flow entirely.
 */
internal fun NavBackStack<NavKey>.exitAccountFlow() {
    while (lastOrNull() is AccountDest) removeLastOrNull()
}
