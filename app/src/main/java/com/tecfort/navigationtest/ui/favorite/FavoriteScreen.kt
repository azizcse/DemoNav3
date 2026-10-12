package com.tecfort.navigationtest.ui.favorite

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.interaction.DragInteraction
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.PermissionController
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
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
    }
}

private data class CarouselItem(val id: Int, val title: String)

private fun dummyCarouselItems(): List<CarouselItem> = (1..16).map { CarouselItem(id = it, title = "Item $it") }

private const val INFINITE_SCROLL_MULTIPLIER = 2000
private const val CAROUSEL_SCROLL_DURATION_MILLIS = 900
private val CAROUSEL_ITEM_SPACING = 8.dp

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
    val density = LocalDensity.current
    var resumeSignal by remember { mutableIntStateOf(0) }

    LaunchedEffect(listState, itemCount) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .collect { index -> currentPage = index % itemCount }
    }

    LaunchedEffect(listState) {
        listState.interactionSource.interactions.collect { interaction ->
            if (interaction is DragInteraction.Stop || interaction is DragInteraction.Cancel) {
                resumeSignal++
            }
        }
    }

    LaunchedEffect(itemCount, resumeSignal) {
        while (true) {
            delay(2000)
            if (listState.isScrollInProgress) continue
            val itemSizePx = listState.layoutInfo.visibleItemsInfo.firstOrNull()?.size ?: 0
            val spacingPx = with(density) { CAROUSEL_ITEM_SPACING.toPx() }
            listState.animateScrollBy(
                value = itemSizePx + spacingPx,
                animationSpec = tween(durationMillis = CAROUSEL_SCROLL_DURATION_MILLIS),
            )
        }
    }

    Column(modifier = modifier) {
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val itemWidth = maxWidth * 0.9f
            LazyRow(
                state = listState,
                horizontalArrangement = Arrangement.spacedBy(CAROUSEL_ITEM_SPACING),
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

private const val INDICATOR_WINDOW_SIZE = 7
private val INDICATOR_NEAR_DOT_SIZE = 8.dp
private val INDICATOR_FAR_DOT_SIZE = 6.dp
private val INDICATOR_FAR_FAR_DOT_SIZE = 4.dp

@Composable
private fun CarouselIndicator(
    currentPage: Int,
    totalPages: Int,
    modifier: Modifier = Modifier,
) {
    val windowSize = minOf(INDICATOR_WINDOW_SIZE, totalPages)
    val windowStart = (currentPage - windowSize / 2).coerceIn(0, (totalPages - windowSize).coerceAtLeast(0))
    val windowIndices = windowStart until windowStart + windowSize
    val leftIndices = windowIndices.filter { it < currentPage }
    val rightIndices = windowIndices.filter { it > currentPage }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IndicatorDotsRow(pageIndices = leftIndices, currentPage = currentPage)
        if (leftIndices.isNotEmpty()) Spacer(modifier = Modifier.width(6.dp))
        AnimatedContent(
            targetState = currentPage,
            transitionSpec = {
                fadeIn(animationSpec = tween(CAROUSEL_SCROLL_DURATION_MILLIS)) togetherWith
                    fadeOut(animationSpec = tween(CAROUSEL_SCROLL_DURATION_MILLIS)) using
                    SizeTransform(clip = false)
            },
            label = "currentPageLabel",
        ) { page ->
            CurrentPageLabel(label = "${page + 1}/$totalPages")
        }
        if (rightIndices.isNotEmpty()) Spacer(modifier = Modifier.width(6.dp))
        IndicatorDotsRow(pageIndices = rightIndices, currentPage = currentPage)
    }
}

@Composable
private fun IndicatorDotsRow(pageIndices: List<Int>, currentPage: Int) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
        userScrollEnabled = false,
    ) {
        items(items = pageIndices, key = { it }) { pageIndex ->
            Box(
                modifier = Modifier.animateItem(
                    placementSpec = tween(durationMillis = CAROUSEL_SCROLL_DURATION_MILLIS),
                ),
                contentAlignment = Alignment.Center,
            ) {
                val distanceFromCurrent = kotlin.math.abs(pageIndex - currentPage)
                val dotSize = when {
                    distanceFromCurrent <= 1 -> INDICATOR_NEAR_DOT_SIZE
                    distanceFromCurrent == 2 -> INDICATOR_FAR_DOT_SIZE
                    else -> INDICATOR_FAR_FAR_DOT_SIZE
                }
                IndicatorDot(dotSize = dotSize)
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
    val animatedDotSize by animateDpAsState(
        targetValue = dotSize,
        animationSpec = tween(durationMillis = CAROUSEL_SCROLL_DURATION_MILLIS),
        label = "indicatorDotSize",
    )
    Box(
        modifier = Modifier
            .size(animatedDotSize)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)),
    )
}
