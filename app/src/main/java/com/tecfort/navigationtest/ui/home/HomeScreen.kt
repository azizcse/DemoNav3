package com.tecfort.navigationtest.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecfort.navigationtest.data.model.Item
import com.tecfort.navigationtest.ui.components.ExpandableFabMenu
import com.tecfort.navigationtest.ui.components.FabAction
import kotlinx.coroutines.launch

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
        )
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
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
        )

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}
