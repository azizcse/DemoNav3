package com.tecfort.navigationtest.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

/** One item in an [ExpandableFabMenu]'s expanded action list. */
data class FabAction(
    val icon: ImageVector,
    val label: String,
    val onClick: () -> Unit,
)

private val CircleRadius = 110.dp

/**
 * A speed-dial FAB anchored to the bottom-right corner. Tapping it (rotating its icon into an
 * X) reveals a scrim plus [actions] arranged in a circle around the center of the screen -
 * tapping the scrim, the main button again, or any action collapses the menu.
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
    Box(modifier = modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn(tween(200)),
            exit = fadeOut(tween(150)),
            modifier = Modifier.fillMaxSize(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { onExpandedChange(false) },
                    ),
            )
        }

        val angleStep = 360f / actions.size
        actions.forEachIndexed { index, action ->
            val angleRad = Math.toRadians((angleStep * index - 90).toDouble())
            val offsetX = CircleRadius * cos(angleRad).toFloat()
            val offsetY = CircleRadius * sin(angleRad).toFloat()

            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn(tween(150, delayMillis = index * 30)) +
                    scaleIn(tween(150, delayMillis = index * 30), initialScale = 0.6f),
                exit = fadeOut(tween(100)) + scaleOut(tween(100), targetScale = 0.6f),
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(x = offsetX, y = offsetY),
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
                .padding(16.dp),
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