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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    HomeScreen(
        uiState = uiState,
        onItemClick = onItemClick,
        modifier = modifier,
    )
}

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onItemClick: (Item) -> Unit,
    modifier: Modifier = Modifier,
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

    ConstraintLayout(modifier = modifier.fillMaxSize()) {
        val (greeting, statsCard, itemsLabel, itemsDivider, list, fab, snackbar) = createRefs()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .constrainAs(greeting) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
        ) {
            Text(text = todaysGreeting(), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(
                text = "Let's crush today's goals",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        HomeStatsCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .constrainAs(statsCard) {
                    top.linkTo(greeting.bottom, margin = 8.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
        )

        Text(
            text = "Items",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .constrainAs(itemsLabel) {
                    top.linkTo(statsCard.bottom, margin = 24.dp)
                    start.linkTo(parent.start)
                },
        )

        HorizontalDivider(
            modifier = Modifier
                .padding(top = 8.dp)
                .constrainAs(itemsDivider) {
                    top.linkTo(itemsLabel.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                },
        )

        LazyColumn(
            modifier = Modifier.constrainAs(list) {
                top.linkTo(itemsDivider.bottom)
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            },
        ) {
            items(uiState.items, key = { it.id }) { item ->
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