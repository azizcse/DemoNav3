package com.tecfort.navigationtest.service

import android.content.Context
import android.graphics.PixelFormat
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

/**
 * Draws a bottom-sheet-style floating window using WindowManager so it can appear
 * over other apps when the SYSTEM_ALERT_WINDOW permission is granted, or above the
 * current activity otherwise (when hosted by a foreground service in the same app).
 */
class OrderOfferOverlay(private val context: Context) {

    private val windowManager: WindowManager =
        context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    private var rootView: View? = null

    fun show(
        offer: OrderOffer,
        onAccept: (OrderOffer) -> Unit,
        onReject: (OrderOffer) -> Unit,
    ) {
        if (rootView != null) return

        val view = buildSheetView(offer, onAccept, onReject)
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            overlayType(),
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
            PixelFormat.TRANSLUCENT,
        ).apply {
            gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
        }

        windowManager.addView(view, params)
        rootView = view
    }

    fun hide() {
        rootView?.let {
            runCatching { windowManager.removeView(it) }
            rootView = null
        }
    }

    private fun overlayType(): Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
    } else {
        @Suppress("DEPRECATION")
        WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
    }

    private fun buildSheetView(
        offer: OrderOffer,
        onAccept: (OrderOffer) -> Unit,
        onReject: (OrderOffer) -> Unit,
    ): View {
        val dp = context.resources.displayMetrics.density

        val container = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding((20 * dp).toInt(), (20 * dp).toInt(), (20 * dp).toInt(), (24 * dp).toInt())
            background = GradientDrawable().apply {
                setColor(0xFFFFFFFF.toInt())
                cornerRadii = floatArrayOf(
                    24 * dp, 24 * dp,
                    24 * dp, 24 * dp,
                    0f, 0f,
                    0f, 0f,
                )
            }
            elevation = 16 * dp
        }

        // Grab handle
        container.addView(View(context).apply {
            background = GradientDrawable().apply {
                setColor(0xFFCCCCCC.toInt())
                cornerRadius = 4 * dp
            }
            layoutParams = LinearLayout.LayoutParams((44 * dp).toInt(), (4 * dp).toInt()).apply {
                gravity = Gravity.CENTER_HORIZONTAL
                bottomMargin = (12 * dp).toInt()
            }
        })

        container.addView(TextView(context).apply {
            text = "New Order Offer"
            textSize = 18f
            setTypeface(typeface, Typeface.BOLD)
            setTextColor(0xFF111111.toInt())
        })

        container.addView(TextView(context).apply {
            text = offer.restaurantName
            textSize = 15f
            setTextColor(0xFF444444.toInt())
            setPadding(0, (6 * dp).toInt(), 0, 0)
        })

        container.addView(TextView(context).apply {
            text = "Pickup: ${offer.pickupAddress}"
            textSize = 13f
            setTextColor(0xFF666666.toInt())
            setPadding(0, (10 * dp).toInt(), 0, 0)
        })

        container.addView(TextView(context).apply {
            text = "Drop-off: ${offer.dropoffAddress}"
            textSize = 13f
            setTextColor(0xFF666666.toInt())
            setPadding(0, (4 * dp).toInt(), 0, 0)
        })

        container.addView(TextView(context).apply {
            text = "%.1f km  •  ৳%.0f".format(offer.distanceKm, offer.payout)
            textSize = 14f
            setTypeface(typeface, Typeface.BOLD)
            setTextColor(0xFF0A7B3E.toInt())
            setPadding(0, (12 * dp).toInt(), 0, 0)
        })

        val buttonRow = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
            ).apply { topMargin = (16 * dp).toInt() }
        }

        buttonRow.addView(Button(context).apply {
            text = "Reject"
            setTextColor(0xFFFFFFFF.toInt())
            background = GradientDrawable().apply {
                setColor(0xFFD32F2F.toInt())
                cornerRadius = 12 * dp
            }
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                .apply { marginEnd = (8 * dp).toInt() }
            setOnClickListener {
                onReject(offer)
                hide()
            }
        })

        buttonRow.addView(Button(context).apply {
            text = "Accept"
            setTextColor(0xFFFFFFFF.toInt())
            background = GradientDrawable().apply {
                setColor(0xFF0A7B3E.toInt())
                cornerRadius = 12 * dp
            }
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                .apply { marginStart = (8 * dp).toInt() }
            setOnClickListener {
                onAccept(offer)
                hide()
            }
        })

        container.addView(buttonRow)
        return container
    }
}
