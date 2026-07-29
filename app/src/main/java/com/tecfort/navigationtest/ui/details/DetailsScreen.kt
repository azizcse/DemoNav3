package com.tecfort.navigationtest.ui.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecfort.navigationtest.ui.components.AppTopBar
import com.tecfort.navigationtest.ui.itemflow.ItemFlowUiState
import com.tecfort.navigationtest.ui.itemflow.ItemFlowViewModel

@Composable
fun DetailsScreen(
    itemId: Int,
    title: String,
    onReviewClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ItemFlowViewModel = hiltViewModel(),
) {
    LaunchedEffect(viewModel, itemId) { viewModel.loadItem(itemId) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    DetailsScreen(
        title = title,
        uiState = uiState,
        onTapClick = viewModel::incrementTapCount,
        onReviewClick = onReviewClick,
        onBackClick = onBackClick,
        modifier = modifier,
    )
}

@Composable
fun DetailsScreen(
    title: String,
    uiState: ItemFlowUiState,
    onTapClick: () -> Unit,
    onReviewClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = { AppTopBar(title = title, onBackClick = onBackClick) },
    ) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator()
                return@Column
            }
            Text(text = "Item #${uiState.item?.id}")
            Text(text = uiState.item?.title ?: "Unknown item")
            Text(text = "Shared taps: ${uiState.tapCount}")
            Text(text = "Shared note: ${uiState.note.ifEmpty { "(none)" }}")
            Button(onClick = onTapClick) { Text("Tap (shared)") }
            Button(onClick = onReviewClick) { Text("Open review") }
        }
    }
}
