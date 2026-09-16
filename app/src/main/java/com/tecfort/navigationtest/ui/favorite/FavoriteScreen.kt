package com.tecfort.navigationtest.ui.favorite

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.health.connect.client.PermissionController
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecfort.navigationtest.data.model.HealthMetrics
import kotlinx.coroutines.delay
import java.util.Locale

@Composable
fun FavoriteScreen(
    modifier: Modifier = Modifier,
    viewModel: FavoriteViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = PermissionController.createRequestPermissionResultContract(),
    ) { viewModel.refresh() }

    FavoriteScreen(
        uiState = uiState,
        onRequestPermissionsClick = { permissionLauncher.launch(uiState.requiredPermissions) },
        modifier = modifier,
    )
}

@Composable
fun FavoriteScreen(
    uiState: FavoriteUiState,
    onRequestPermissionsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        FavoriteCarousel(
            carouselItems = dummyCarouselItems(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
        )

        val contentModifier = Modifier.weight(1f).fillMaxWidth()

        when {
            uiState.isLoading -> Box(contentModifier, contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }

            !uiState.isHealthConnectAvailable -> Box(contentModifier, contentAlignment = Alignment.Center) {
                Text(
                    text = "Health Connect isn't available on this device.",
                    modifier = Modifier.padding(16.dp),
                )
            }

            !uiState.hasPermissions -> Column(
                modifier = contentModifier.padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Grant Health Connect permissions to see your activity data.",
                    modifier = Modifier.padding(bottom = 16.dp),
                )
                Button(onClick = onRequestPermissionsClick) {
                    Text("Grant access")
                }
            }

            else -> LazyColumn(modifier = contentModifier) {
                items(healthMetricRows(uiState.metrics), key = { it.label }) { row ->
                    ListItem(
                        headlineContent = { Text(row.label) },
                        trailingContent = { Text(row.value) },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}

private data class CarouselItem(val id: Int, val title: String)

private fun dummyCarouselItems(): List<CarouselItem> = (1..15).map { CarouselItem(id = it, title = "Item $it") }

private const val INFINITE_SCROLL_MULTIPLIER = 2000

@Composable
private fun FavoriteCarousel(
    carouselItems: List<CarouselItem>,
    modifier: Modifier = Modifier,
) {
    if (carouselItems.isEmpty()) return

    val itemCount = carouselItems.size
    val virtualItemCount = itemCount * INFINITE_SCROLL_MULTIPLIER
    val startIndex = remember(itemCount) { (virtualItemCount / 2 / itemCount) * itemCount }

    val listState = rememberLazyListState(initialFirstVisibleItemIndex = startIndex)
    var currentPage by remember { mutableIntStateOf(0) }

    LaunchedEffect(listState, itemCount) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .collect { index -> currentPage = index % itemCount }
    }

    LaunchedEffect(itemCount) {
        while (true) {
            delay(1000)
            listState.animateScrollToItem(listState.firstVisibleItemIndex + 1)
        }
    }

    Column(modifier = modifier) {
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val itemWidth = maxWidth * 0.9f
            LazyRow(
                state = listState,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(count = virtualItemCount, key = { it }) { virtualIndex ->
                    CarouselCard(
                        item = carouselItems[virtualIndex % itemCount],
                        modifier = Modifier.width(itemWidth),
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        CarouselIndicator(
            currentPage = currentPage,
            totalPages = itemCount,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun CarouselCard(
    item: CarouselItem,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.height(160.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
            )
        }
    }
}

private const val INDICATOR_WINDOW_SIZE = 5
private val INDICATOR_NEAR_DOT_SIZE = 8.dp
private val INDICATOR_FAR_DOT_SIZE = 6.dp

@Composable
private fun CarouselIndicator(
    currentPage: Int,
    totalPages: Int,
    modifier: Modifier = Modifier,
) {
    val windowSize = minOf(INDICATOR_WINDOW_SIZE, totalPages)
    val windowStart = (currentPage - 2).coerceIn(0, (totalPages - windowSize).coerceAtLeast(0))

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        for (slot in 0 until windowSize) {
            val pageIndex = windowStart + slot
            if (slot != 0) {
                Spacer(modifier = Modifier.width(6.dp))
            }
            if (pageIndex == currentPage) {
                CurrentPageLabel(label = "${pageIndex + 1}/$totalPages")
            } else {
                key(pageIndex) {
                    val distanceFromCurrent = kotlin.math.abs(pageIndex - currentPage)
                    IndicatorDot(
                        dotSize = if (distanceFromCurrent <= 1) INDICATOR_NEAR_DOT_SIZE else INDICATOR_FAR_DOT_SIZE,
                    )
                }
            }
        }
    }
}

@Composable
private fun CurrentPageLabel(label: String) {
    Text(
        text = label,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onPrimary,
        modifier = Modifier
            .clip(RoundedCornerShape(percent = 50))
            .background(MaterialTheme.colorScheme.primary)
            .padding(horizontal = 10.dp, vertical = 4.dp),
    )
}

@Composable
private fun IndicatorDot(dotSize: Dp) {
    val animatedDotSize by animateDpAsState(targetValue = dotSize, label = "indicatorDotSize")
    Box(
        modifier = Modifier
            .size(animatedDotSize)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)),
    )
}

private data class HealthMetricRow(val label: String, val value: String)

private fun healthMetricRows(metrics: HealthMetrics): List<HealthMetricRow> = listOf(
    HealthMetricRow("Steps", metrics.steps.toString()),
    HealthMetricRow("Distance", metrics.distanceMeters.format(unit = "m")),
    HealthMetricRow("Elevation gained", metrics.elevationGainedMeters.format(unit = "m")),
    HealthMetricRow("Floors climbed", metrics.floorsClimbed.format(unit = "")),
    HealthMetricRow("Power", metrics.averagePowerWatts.format(unit = "W")),
    HealthMetricRow("Speed", metrics.averageSpeedMetersPerSecond.format(unit = "m/s")),
    HealthMetricRow("Cadence", metrics.averageCadenceRpm.format(unit = "rpm")),
)

private fun Double?.format(unit: String): String {
    if (this == null) return "No data"
    val number = String.format(Locale.getDefault(), "%.1f", this)
    return if (unit.isEmpty()) number else "$number $unit"
}
