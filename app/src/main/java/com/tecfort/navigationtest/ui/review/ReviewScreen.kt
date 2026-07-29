package com.tecfort.navigationtest.ui.review

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
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
fun ReviewScreen(
    itemId: Int,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ItemFlowViewModel = hiltViewModel(),
) {
    LaunchedEffect(viewModel, itemId) { viewModel.loadItem(itemId) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ReviewScreen(
        uiState = uiState,
        onTapClick = viewModel::incrementTapCount,
        onNoteChange = viewModel::onNoteChange,
        onBackClick = onBackClick,
        modifier = modifier,
    )
}

@Composable
fun ReviewScreen(
    uiState: ItemFlowUiState,
    onTapClick: () -> Unit,
    onNoteChange: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = { AppTopBar(title = "Review", onBackClick = onBackClick) },
    ) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator()
                return@Column
            }
            // Item id/title come from the shared ViewModel only - never passed in as an
            // argument - which is the visible proof that Details and Review share one instance.
            Text(text = "Item #${uiState.item?.id}")
            Text(text = uiState.item?.title ?: "Unknown item")
            Text(text = "Shared taps: ${uiState.tapCount}")
            Button(onClick = onTapClick) { Text("Tap (shared)") }
            OutlinedTextField(
                value = uiState.note,
                onValueChange = onNoteChange,
                label = { Text("Shared note") },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
