package com.tecfort.navigationtest.navigation

import kotlinx.serialization.Serializable

/** Shared flow scope id for every [AccountDest] member - see [AccountDest] KDoc. */
internal const val ACCOUNT_SCOPE_ID = "account-security-flow"

/**
 * The account-security route group, reachable from [Dest.ProfileSetting]: change password,
 * forgot password, and OTP verification. Every member shares one
 * `AccountSecurityViewModel` via a single flow scope (see [FlowScopedNavKey],
 * [flowScopedEntry]), so the email entered on [ForgotPassword] and the code verified on [Otp]
 * are visible to [ChangePassword] without being passed forward as nav arguments.
 */
sealed interface AccountDest : Dest, FlowScopedNavKey {
    override val scopeId: String get() = ACCOUNT_SCOPE_ID

    /**
     * @param isReset true when arriving here from a verified OTP code - hides the
     *   current-password field, since identity was already proven by the code. False for the
     *   normal "change my password" entry point, which requires the current password.
     */
    @Serializable
    data class ChangePassword(val isReset: Boolean = false) : AccountDest

    @Serializable
    data object ForgotPassword : AccountDest

    /** @param email the address a code was just sent to, shown on screen for confirmation. */
    @Serializable
    data class Otp(val email: String) : AccountDest
}
