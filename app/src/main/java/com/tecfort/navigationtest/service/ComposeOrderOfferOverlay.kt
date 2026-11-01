package com.tecfort.navigationtest.service

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner

/**
 * Compose-based floating bottom sheet rendered through [WindowManager].
 *
 * Hosts a [ComposeView] with its own [LifecycleOwner], [SavedStateRegistryOwner],
 * and [ViewModelStoreOwner] so Compose primitives like `remember` and
 * `rememberSaveable` work inside a Service context.
 */
class ComposeOrderOfferOverlay(
    private val context: Context,
) : LifecycleOwner, SavedStateRegistryOwner, ViewModelStoreOwner {

    private val windowManager: WindowManager =
        context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val savedStateRegistryController = SavedStateRegistryController.create(this)
    private val store = ViewModelStore()

    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val savedStateRegistry: SavedStateRegistry
        get() = savedStateRegistryController.savedStateRegistry
    override val viewModelStore: ViewModelStore get() = store

    private var composeView: ComposeView? = null

    init {
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
    }

    fun show(
        offer: OrderOffer,
        onAccept: (OrderOffer) -> Unit,
        onReject: (OrderOffer) -> Unit,
    ) {
        if (composeView != null) return

        val view = ComposeView(context).apply {
            setViewTreeLifecycleOwner(this@ComposeOrderOfferOverlay)
            setViewTreeSavedStateRegistryOwner(this@ComposeOrderOfferOverlay)
            setViewTreeViewModelStoreOwner(this@ComposeOrderOfferOverlay)
            setContent {
                FloatingOrderBottomSheetContent(
                    offer = offer,
                    onAccept = {
                        onAccept(offer)
                        hide()
                    },
                    onDismiss = {
                        onReject(offer)
                        hide()
                    },
                )
            }
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            overlayType(),
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH or
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
            PixelFormat.TRANSLUCENT,
        ).apply {
            gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
            x = 0
            y = 0
        }

        lifecycleRegistry.currentState = Lifecycle.State.RESUMED
        windowManager.addView(view, params)
        composeView = view
    }

    fun hide() {
        val view: View? = composeView
        if (view != null) {
            runCatching { windowManager.removeView(view) }
            composeView = null
        }
        if (lifecycleRegistry.currentState != Lifecycle.State.DESTROYED) {
            lifecycleRegistry.currentState = Lifecycle.State.CREATED
        }
    }

    fun destroy() {
        hide()
        lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
        store.clear()
    }

    private fun overlayType(): Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
    } else {
        @Suppress("DEPRECATION")
        WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
    }
}
