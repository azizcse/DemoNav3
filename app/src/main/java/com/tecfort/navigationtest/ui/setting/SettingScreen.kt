package com.tecfort.navigationtest.ui.setting

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tsl.pricetags.drawDottedBorder
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SettingScreen(
    profileDisplayName: String,
    onEditProfileClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val asymmetricalShape = CutCornerShape(
        topStartPercent = 50,
        topEndPercent = 20, // Smaller cut here
        bottomStartPercent = 50,
        bottomEndPercent = 50
    )

    val shape = CutCornerShape(
        topStartPercent = 0,
        topEndPercent = 50,
        bottomStartPercent = 50,
        bottomEndPercent = 50
    )

    /*   val diamondShape = CutCornerShape(
         topStartPercent = 50,
         topEndPercent = 50,
         bottomStartPercent = 50,
         bottomEndPercent = 50
     )*/

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Card(
            modifier = Modifier.size(120.dp),
            shape = asymmetricalShape,
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFFA2F47)
            ),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .drawDottedBorder(shape = asymmetricalShape)
            ) {
                Text(
                    "FLASH \n SALE",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .rotate(-45f),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Card(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(18.dp)
                        .size(16.dp),
                    shape = CircleShape
                ) {}
            }
        }

        //////////
        Spacer(modifier = Modifier.height(30.dp))

        Card(
            modifier = Modifier.size(80.dp),
            shape = CutCornerShape(
                topStartPercent = 50,
                topEndPercent = 50,
                bottomStartPercent = 50,
                bottomEndPercent = 50
            ),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFFA2F47)
            ),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Text(
                    "50%\nOFF",
                    modifier = Modifier
                        .align(Alignment.Center),
                    color = Color.White,
                    fontWeight = FontWeight.Bold, fontSize = 20.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
        /////

        Card(
            modifier = Modifier.size(120.dp),
            shape = shape,
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFA2F47)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    // We use drawWithContent to draw the border over the red background
                    .drawWithContent {
                        drawContent() // Draws the text and the small circle first

                        val padding = 8.dp.toPx()
                        val outline = shape.createOutline(
                            size = size.copy(
                                width = size.width - (padding * 2),
                                height = size.height - (padding * 2)
                            ),
                            layoutDirection = layoutDirection,
                            density = this
                        )

                        // 2. Translate (offset) the canvas so the 8dp padding applies to all sides
                        withTransform({
                            translate(left = padding, top = padding)
                        }) {
                            drawOutline(
                                outline = outline,
                                color = Color.White,
                                style = Stroke(
                                    width = 2.dp.toPx(),
                                    pathEffect = PathEffect.dashPathEffect(
                                        intervals = floatArrayOf(10f, 10f),
                                        phase = 0f
                                    )
                                )
                            )
                        }
                    }
                    .padding(5.dp) // Ensures internal content stays inside the border
            ) {
                // ... Your existing Text and Card content here
                Text(
                    "50%\nOFF",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .rotate(-45f),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                )
                // Small punch hole
                Card(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .size(16.dp),
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {}
            }

        }
        Spacer(modifier = Modifier.height(30.dp))

        Box(
            modifier = Modifier.size(50.dp),
            contentAlignment = Alignment.Center
        ) {
            Decagon(modifier = Modifier.fillMaxSize())
            Text(
                "20%\nOFF",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        Box(
            modifier = Modifier
                .size(150.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFFEEEEEE))
        ) {
            CornerRibbon2(
                text = "20% OFF",
                modifier = Modifier.align(Alignment.TopStart)
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        Box(
            modifier = Modifier
                .size(150.dp)
                .aspectRatio(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFF5F5F5))
        ) {
            /*AsyncImage(
                model = "imageUrl",
                contentDescription = "title",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
*/
            // Corner Ribbon Tag
            CornerRibbon(
                text = "15% OFF",
                modifier = Modifier.align(Alignment.TopStart)
            )

            // Bottom-right floating add button
            FloatingActionButton(
                onClick = { },
                shape = CircleShape,
                containerColor = Color(0xFF00C853),
                contentColor = Color.White,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
                    .size(36.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add to Cart")
            }
        }
        Spacer(modifier = Modifier.height(30.dp))
        NotificationCard(onDismiss = {})

        Spacer(modifier = Modifier.height(30.dp))
        Button(onClick = onEditProfileClick) { Text("Edit profile") }
        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
fun CornerRibbon(
    text: String,
    modifier: Modifier = Modifier,
    size: Dp = 70.dp,
    color: Color = Color(0xFFFA2F47),
) {
    // The ribbon lives in a square box the size of the top-left corner it covers.
    Box(modifier = modifier.size(size)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // The band is the strip between two 45° diagonal lines: x + y = a and x + y = b.
            // `a` is where the inner (short) edge hits the top and left sides,
            // `b` is where the outer (long) edge hits them, both as a fraction of the width.
            val a = this.size.width * 0.35f
            val b = this.size.width * 0.85f

            // Trace the band as a 4-point polygon (a trapezoid):
            //   (a,0) ──── (b,0)      top edge
            //     \          /
            //   (0,a) ──── (0,b)      left edge
            // moveTo -> start on the top edge at the inner diagonal,
            // lineTo(b,0) -> slide right along the top edge to the outer diagonal,
            // lineTo(0,b) -> cut diagonally down-left along the outer line to the left edge,
            // lineTo(0,a) -> move up the left edge to the inner diagonal,
            // close() -> cut diagonally back up-right along the inner line to the start.
            val band = Path().apply {
                moveTo(a, 0f)
                lineTo(b, 0f)
                lineTo(0f, b)
                lineTo(0f, a)
                close()
            }
            drawPath(band, color)
        }
        // The band's centre line is x + y = (a + b) / 2 = 0.6 * size, so its midpoint is at
        // (0.3 * size, 0.3 * size). Text starts centred in the box (0.5 * size), so shift it
        // by -0.2 * size on both axes, then rotate -45° (around its own centre) to follow the band.
        Text(
            text,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(x = -size * 0.2f, y = -size * 0.2f)
                .rotate(-45f),
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 8.sp
        )
    }
}

/**
 * A diagonal ribbon banner shown across the top-start corner of a 70dp square.
 *
 * The ribbon is a rotated, padded [Box] holding [text]. The outer container clips
 * to its bounds, so the parts of the ribbon that extend past the square are cut off,
 * giving the typical "corner sash" look.
 *
 * @param text Label displayed on the ribbon (e.g. "NEW", "SALE").
 * @param modifier Modifier applied to the outer 70dp container.
 * @param color Background color of the ribbon.
 * @param textColor Color of the ribbon label.
 */
@Composable
fun CornerRibbon2(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color(0xFFD32F2F),
    textColor: Color = Color.White
) {
    // Fixed-size square that clips the ribbon so it doesn't overflow the corner
    Box(
        modifier = modifier
            .size(70.dp)
            .clipToBounds()
    ) {
        // The ribbon itself: shifted toward the corner, then rotated -45° to run diagonally
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = (-22).dp, y = (10).dp) // Adjust position diagonally
                .rotate(-45f) // Rotates ribbon across the corner
                .background(color)
                .padding(vertical = 4.dp, horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            // Ribbon label, kept to a single line so it stays within the sash
            Text(
                text = text,
                color = textColor,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
        }
    }
}

@Composable
fun NotificationCard(onDismiss: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "System Update Available",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Version 2.4 contains security fixes and UI enhancements.",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(12.dp))

            // wrapContentWidth aligns a wrap_content button to the right end
            TextButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.End)
                    .wrapContentWidth()
            ) {
                Text("Dismiss")
            }
        }
    }
}
/* Column(
     modifier.fillMaxSize(),
     verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
     horizontalAlignment = Alignment.CenterHorizontally,
 ) {
     Text(
         if (profileDisplayName.isEmpty()) {
             "No display name set"
         } else {
             "Display name: $profileDisplayName"
         },
     )
     Button(onClick = onEditProfileClick) { Text("Edit profile") }
 }*/


@Composable
fun Decagon(
    modifier: Modifier = Modifier.size(60.dp),
    color: Color = Color(0xFFFA2F47),
    isFilled: Boolean = false,
    strokeWidth: Dp = 4.dp,
    curveDepth: Float = 0.15f
) {
    Canvas(modifier = modifier) {
        val sides = 15
        val center = center
        val radius = size.minDimension / 2f
        val angleStep = (2 * Math.PI / sides).toFloat()

        // Vertex i, with the first vertex pointing straight up
        fun vertex(i: Int): Offset {
            val angle = i * angleStep - (Math.PI / 2).toFloat()
            return Offset(center.x + radius * cos(angle), center.y + radius * sin(angle))
        }

        val path = Path().apply {
            val start = vertex(0)
            moveTo(start.x, start.y)
            for (i in 0 until sides) {
                val end = vertex((i + 1) % sides)
                // Control point sits on the edge's midpoint ray, pulled toward the center
                val midAngle = (i + 0.5f) * angleStep - (Math.PI / 2).toFloat()
                val edgeMidDistance = radius * cos(angleStep / 2)
                val controlDistance = edgeMidDistance * (1f - curveDepth)
                quadraticTo(
                    center.x + controlDistance * cos(midAngle),
                    center.y + controlDistance * sin(midAngle),
                    end.x,
                    end.y
                )
            }
            close()
        }

        if (isFilled) {
            drawPath(path = path, color = color)
        } else {
            drawPath(
                path = path,
                color = color,
                style = Stroke(width = strokeWidth.toPx())
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingScreenPreview() {
    SettingScreen(
        profileDisplayName = "John Doe",
        onEditProfileClick = {},
    )
}
