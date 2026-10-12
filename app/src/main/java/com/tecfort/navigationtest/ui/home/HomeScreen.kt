package com.tecfort.navigationtest.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.FilledIconButton
import androidx.compose.foundation.layout.width
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import com.tecfort.navigationtest.ui.components.CollapsibleHeader
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecfort.navigationtest.data.model.Item
import com.tecfort.navigationtest.ui.components.ExpandableFabMenu
import com.tecfort.navigationtest.ui.components.FabAction
import kotlinx.coroutines.launch
import java.time.LocalTime

@Composable
fun HomeScreen(
    onItemClick: (Item) -> Unit,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onCalendarClick: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    HomeScreen(
        uiState = uiState,
        onItemClick = onItemClick,
        modifier = modifier,
        onBackClick = onBackClick,
        onCalendarClick = onCalendarClick,
    )
}

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onItemClick: (Item) -> Unit,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onCalendarClick: () -> Unit = {},
) {
    if (uiState.isLoading) {
        Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var fabExpanded by rememberSaveable { mutableStateOf(false) }
    val fabActions = remember(coroutineScope) {
        listOf(
            FabAction(Icons.Filled.Restaurant, "Add Food") {
                coroutineScope.launch { snackbarHostState.showSnackbar("Add Food tapped") }
            },
            FabAction(Icons.Filled.WaterDrop, "Log Water") {
                coroutineScope.launch { snackbarHostState.showSnackbar("Log Water tapped") }
            },
            FabAction(Icons.Filled.FitnessCenter, "Add Exercise") {
                coroutineScope.launch { snackbarHostState.showSnackbar("Add Exercise tapped") }
            },
            FabAction(Icons.Filled.MonitorWeight, "Log Weight") {
                coroutineScope.launch { snackbarHostState.showSnackbar("Log Weight tapped") }
            },
            FabAction(Icons.Default.MonitorHeart, "Exercise") {
                coroutineScope.launch { snackbarHostState.showSnackbar("Log Weight tapped") }
            },
        )
    }

    var query by rememberSaveable { mutableStateOf("") }
    val filteredItems = remember(uiState.items, query) {
        if (query.isBlank()) uiState.items else uiState.items.filter { it.title.contains(query, ignoreCase = true) }
    }

    ConstraintLayout(modifier = modifier.fillMaxSize()) {
        val (header, fab, snackbar) = createRefs()

        CollapsibleHeader(
            modifier = Modifier.constrainAs(header) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            },
            headerContent = { progress -> HomeHeaderContent(collapseProgress = progress) },
            searchBarHorizontalPadding = 4.dp,
            searchBar = { progress ->
                HomeSearchRow(
                    collapseProgress = progress,
                    query = query,
                    onQueryChange = { query = it },
                    onBackClick = onBackClick,
                    onCalendarClick = {
                        onCalendarClick()
                        coroutineScope.launch { snackbarHostState.showSnackbar("Calendar tapped") }
                    },
                )
            },
        ) {
            items(filteredItems, key = { it.id }) { item ->
                ListItem(
                    headlineContent = { Text(item.title) },
                    modifier = Modifier.clickable { onItemClick(item) },
                )
                HorizontalDivider()
            }
        }

        ExpandableFabMenu(
            expanded = fabExpanded,
            onExpandedChange = { fabExpanded = it },
            actions = fabActions,
            modifier = Modifier.constrainAs(fab) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            },
        )

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.constrainAs(snackbar) {
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            },
        )
    }
}

private fun todaysGreeting(): String = when (LocalTime.now().hour) {
    in 5..11 -> "Good morning"
    in 12..16 -> "Good afternoon"
    else -> "Good evening"
}

@Composable
private fun HomeHeaderContent(collapseProgress: Float) {
    val expandedAlpha = (1f - collapseProgress * 2f).coerceIn(0f, 1f)
    Box(modifier = Modifier.fillMaxSize()) {
        // Expanded: greeting + stats. Fades out in the first half of the collapse.
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .graphicsLayer { alpha = expandedAlpha },
        ) {
            Text(text = todaysGreeting(), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(
                text = "Let's crush today's goals",
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(modifier = Modifier.height(12.dp))
            HomeStatsCard(modifier = Modifier.fillMaxWidth())
        }
        // Collapsed: the floating search field is the only header content.
    }
}

/**
 * Search field with a back button on its left and a circular calendar button on its right.
 * Both buttons grow in (width and alpha) as the header collapses; they are inert until visible.
 */
@Composable
private fun HomeSearchRow(
    collapseProgress: Float,
    query: String,
    onQueryChange: (String) -> Unit,
    onBackClick: () -> Unit,
    onCalendarClick: () -> Unit,
) {
    val buttonSize = 48.dp
    val gap = 8.dp
    val progress = collapseProgress.coerceIn(0f, 1f)
    val enabled = progress >= 0.8f
    val buttonAlpha = ((progress - 0.5f) * 2f).coerceIn(0f, 1f)

    Row(
        // The header gives the row a small base inset; the rest shrinks away as it collapses
        // so the buttons sit close to the screen edges while the expanded field keeps 16dp.
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp * (1f - progress)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .width(buttonSize * progress)
                .graphicsLayer { alpha = buttonAlpha }
                .clip(CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            IconButton(onClick = onBackClick, enabled = enabled, modifier = Modifier.size(buttonSize)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        }
        Spacer(modifier = Modifier.width(gap * progress))
        Box(modifier = Modifier.weight(1f)) {
            HomeSearchField(query = query, onQueryChange = onQueryChange)
        }
        Spacer(modifier = Modifier.width(gap * progress))
        Box(
            modifier = Modifier
                .width(buttonSize * progress)
                .graphicsLayer { alpha = buttonAlpha }
                .clip(CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            FilledIconButton(onClick = onCalendarClick, enabled = enabled, modifier = Modifier.size(buttonSize)) {
                Icon(Icons.Filled.CalendarMonth, contentDescription = "Calendar")
            }
        }
    }
}

@Composable
private fun HomeSearchField(query: String, onQueryChange: (String) -> Unit) {
    Surface(
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        TextField(
            value = query,
            onValueChange = onQueryChange,
            singleLine = true,
            placeholder = { Text("Search items") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(Icons.Filled.Close, contentDescription = "Clear")
                    }
                }
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun HomeStatsCard(modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            HomeStat(icon = Icons.AutoMirrored.Filled.DirectionsWalk, label = "Steps", value = "100")
            HomeStat(icon = Icons.Filled.MonitorHeart, label = "Heart", value = "100")
            HomeStat(icon = Icons.Filled.LocalFireDepartment, label = "Calories", value = "100")
            HomeStat(icon = Icons.Filled.WaterDrop, label = "Water", value = "100")
        }
    }
}

@Composable
private fun HomeStat(icon: ImageVector, label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
        Text(text = label, style = MaterialTheme.typography.labelSmall)
    }
}