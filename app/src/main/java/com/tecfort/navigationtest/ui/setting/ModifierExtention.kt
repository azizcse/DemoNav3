package com.tsl.pricetags


import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.drawDottedBorder(
    shape: Shape,
    color: Color = Color.White,
    strokeWidth: Dp = 2.dp,
    dashLength: Float = 5f,
    gapLength: Float = 10f,
    padding: Dp = 8.dp
) = this.drawWithContent {
    // 1. Draw the actual Composable content first
    drawContent()
    val paddingPx = padding.toPx()
    val strokeWidthPx = strokeWidth.toPx()
    // 2. Create the scaled-down outline based on the provided shape
    val outline = shape.createOutline(
        size = size.copy(
            width = size.width - (paddingPx * 2),
            height = size.height - (paddingPx * 2),
        ),
        layoutDirection = layoutDirection,
        density = this
    )
    // 3. Draw the path with the dash effect

    withTransform({
        translate(left = paddingPx, top = paddingPx)
    }) {
        drawOutline(
            outline = outline,
            color = color,
            style = Stroke(
                width = strokeWidthPx,
                pathEffect = PathEffect.dashPathEffect(
                    intervals = floatArrayOf(dashLength, gapLength),
                    phase = 0f
                ),
                cap = StrokeCap.Round
            )
        )
    }

}
