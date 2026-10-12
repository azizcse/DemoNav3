package com.tecfort.navigationtest.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.zIndex

object CollapsibleHeaderDefaults {
    val MaxHeaderHeight = 220.dp
    val MinHeaderHeight = 72.dp
    val SearchBarHeight = 56.dp
    val SearchBarBodyGap = 8.dp
}

/**
 * Header that shrinks from [maxHeaderHeight] to [minHeaderHeight] as [bodyContent] scrolls.
 *
 * - [headerContent] fills the header; it receives the collapse progress (0 = expanded, 1 = collapsed)
 *   so callers can fade or swap their own title/content.
 * - [searchBar] floats on the bottom edge of the expanded header and glides into the vertical
 *   centre of the collapsed header.
 */
@Composable
fun CollapsibleHeader(
    headerContent: @Composable (collapseProgress: Float) -> Unit,
    searchBar: @Composable (collapseProgress: Float) -> Unit,
    modifier: Modifier = Modifier,
    maxHeaderHeight: Dp = CollapsibleHeaderDefaults.MaxHeaderHeight,
    minHeaderHeight: Dp = CollapsibleHeaderDefaults.MinHeaderHeight,
    searchBarHorizontalPadding: Dp = 16.dp,
    listState: LazyListState = rememberLazyListState(),
    bodyContent: LazyListScope.() -> Unit,
) {
    val density = LocalDensity.current
    val maxScrollPx = with(density) { (maxHeaderHeight - minHeaderHeight).toPx().coerceAtLeast(1f) }

    // Collapse is driven by scroll deltas (not list offset), so it tracks the finger 1:1 no matter
    // how tall the first list item is. Scrolling up collapses first, then the list scrolls;
    // scrolling down scrolls the list to the top first, then expands.
    var collapsePx by remember { mutableFloatStateOf(0f) }
    val nestedScrollConnection = remember(maxScrollPx) {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (available.y >= 0f) return Offset.Zero
                val newValue = (collapsePx - available.y).coerceIn(0f, maxScrollPx)
                val consumed = newValue - collapsePx
                collapsePx = newValue
                return Offset(0f, -consumed)
            }

            override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
                if (available.y <= 0f) return Offset.Zero
                val newValue = (collapsePx - available.y).coerceIn(0f, maxScrollPx)
                val used = collapsePx - newValue
                collapsePx = newValue
                return Offset(0f, used)
            }
        }
    }
    val progress = (collapsePx / maxScrollPx).coerceIn(0f, 1f)

    var searchBarHeight by remember { mutableStateOf(CollapsibleHeaderDefaults.SearchBarHeight) }

    val headerHeight = lerp(maxHeaderHeight, minHeaderHeight, progress)
    val searchCenterY = lerp(maxHeaderHeight, minHeaderHeight / 2, progress)
    val searchOffsetY = searchCenterY - searchBarHeight / 2

    // Fixed top padding so the first item clears the search bar overhang while expanded.
    // It must not depend on [progress], otherwise the list offset would feed back into itself.
    val bodyTopPadding = searchBarHeight / 2 + CollapsibleHeaderDefaults.SearchBarBodyGap

    Box(modifier = modifier.fillMaxSize().nestedScroll(nestedScrollConnection)) {
        Surface(
            color = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            shadowElevation = lerp(0.dp, 8.dp, progress),
            modifier = Modifier
                .fillMaxWidth()
                .height(headerHeight)
                .zIndex(1f),
        ) {
            headerContent(progress)
        }

        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(top = bodyTopPadding),
            modifier = Modifier
                .fillMaxSize()
                .padding(top = headerHeight),
            content = bodyContent,
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .zIndex(2f)
                .padding(horizontal = searchBarHorizontalPadding)
                .onSizeChanged { searchBarHeight = with(density) { it.height.toDp() } }
                .offset(y = searchOffsetY),
        ) {
            searchBar(progress)
        }
    }
}
