package com.tecfort.navigationtest.navigation

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Process-wide channel for navigation requests coming from outside the composition
 * (e.g. a floating overlay triggered by a background service). [NavigationRoot]
 * collects this and pushes the destination onto its back stack.
 */
object PendingNavigation {

    private val _requests = MutableSharedFlow<Dest>(
        replay = 1,
        extraBufferCapacity = 4,
    )

    val requests: SharedFlow<Dest> = _requests.asSharedFlow()

    fun navigateTo(dest: Dest) {
        _requests.tryEmit(dest)
    }

    /** Called by [NavigationRoot] once a request has been pushed onto the back stack. */
    fun consumed() {
        _requests.resetReplayCache()
    }
}
