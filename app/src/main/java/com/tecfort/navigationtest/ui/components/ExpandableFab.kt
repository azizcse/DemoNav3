package com.tecfort.navigationtest.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin
import kotlin.time.Duration.Companion.milliseconds

/** One item in an [ExpandableFabMenu]'s expanded action list. */
data class FabAction(
    val icon: ImageVector,
    val label: String,
    val onClick: () -> Unit,
)

private val CircleRadius = 110.dp
private val FabRadius = 28.dp
private val FabCornerPadding = 16.dp
private const val StaggerDelayMs = 30L
private const val TravelDurationMs = 280

/**
 * A speed-dial FAB anchored to the bottom-right corner. Tapping it (rotating its icon into an
 * X) reveals a scrim plus [actions], each of which animates outward from the button's own
 * position into its spot on a circle centered on the screen - and animates back into the button
 * on collapse. Tapping the scrim, the main button again, or any action collapses the menu.
 *
 * Meant to be layered as a full-size overlay above the screen's content (e.g. in a `Box` next
 * to a `LazyColumn`), so the scrim and centered circle can cover the whole screen regardless of
 * where the composable is placed in the tree.
 */
@Composable
fun ExpandableFabMenu(
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    actions: List<FabAction>,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        // Vector from screen center to the FAB's own center, so items can start/end there.
        val fabOffsetX = maxWidth / 2 - FabCornerPadding - FabRadius
        val fabOffsetY = maxHeight / 2 - FabCornerPadding - FabRadius

        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn(tween(200)),
            exit = fadeOut(tween(150)),
            modifier = Modifier.fillMaxSize(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Gray.copy(alpha = 0.4f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { onExpandedChange(false) },
                    ),
            )
        }

        val angleStep = 360f / actions.size
        // One Animatable per action: 0 = collapsed (sitting on the FAB), 1 = expanded (at its
        // circle spot). Staggering the launch of each keeps them from moving in lockstep, so
        // they visibly cascade out of - and back into - the FAB.
        val progresses = remember(actions) { actions.map { Animatable(0f) } }
        LaunchedEffect(expanded, progresses) {
            progresses.forEachIndexed { index, progress ->
                launch {
                    delay((index * StaggerDelayMs).milliseconds)
                    progress.animateTo(
                        targetValue = if (expanded) 1f else 0f,
                        animationSpec = tween(TravelDurationMs),
                    )
                }
            }
        }

        actions.forEachIndexed { index, action ->
            val itemProgress = progresses[index].value
            if (itemProgress <= 0f) return@forEachIndexed

            val angleRad = Math.toRadians((angleStep * index - 90).toDouble())
            val targetX = CircleRadius * cos(angleRad).toFloat()
            val targetY = CircleRadius * sin(angleRad).toFloat()

            val currentX = fabOffsetX + (targetX - fabOffsetX) * itemProgress
            val currentY = fabOffsetY + (targetY - fabOffsetY) * itemProgress
            val scale = 0.3f + 0.7f * itemProgress

            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(x = currentX, y = currentY)
                    .graphicsLayer(scaleX = scale, scaleY = scale, alpha = itemProgress),
            ) {
                CircularFabAction(
                    action = action,
                    onClick = {
                        onExpandedChange(false)
                        action.onClick()
                    },
                )
            }
        }

        val rotation by animateFloatAsState(targetValue = if (expanded) 45f else 0f, label = "fabRotation")
        FloatingActionButton(
            onClick = { onExpandedChange(!expanded) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(FabCornerPadding),
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = if (expanded) "Close menu" else "Open menu",
                modifier = Modifier.rotate(rotation),
            )
        }
    }
}

@Composable
private fun CircularFabAction(action: FabAction, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            onClick = onClick,
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.primaryContainer,
            tonalElevation = 3.dp,
        ) {
            Box(
                modifier = Modifier.padding(16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = action.icon,
                    contentDescription = action.label,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }
        Text(
            text = action.label,
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .padding(top = 4.dp)
                .width(72.dp),
        )
    }
}