package com.tecfort.navigationtest.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.tecfort.navigationtest.R
import kotlinx.coroutines.delay

private const val ANIMATION_DURATION_MS = 600
private const val HOLD_DURATION_MS = 600L

/**
 * Branded splash shown right after the system splash screen dismisses. Animates the
 * logo in with a scale + fade, holds briefly, then calls [onTimeout] once.
 */
@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
    onTimeout: () -> Unit,
) {
    val scale = remember { Animatable(0.7f) }
    var alpha by remember { mutableFloatStateOf(0f) }
    val currentOnTimeout by rememberUpdatedState(onTimeout)

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = ANIMATION_DURATION_MS, easing = FastOutSlowInEasing)
        )
        alpha = 1f
        delay(HOLD_DURATION_MS)
        currentOnTimeout()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_splash_logo),
            contentDescription = null,
            modifier = Modifier
                .size(120.dp)
                .scale(scale.value)
                .alpha(alpha),
        )
    }
}
