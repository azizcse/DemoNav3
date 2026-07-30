package com.tecfort.navigationtest.ui.favorite

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.health.connect.client.PermissionController
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecfort.navigationtest.data.model.HealthMetrics
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
    when {
        uiState.isLoading -> Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }

        !uiState.isHealthConnectAvailable -> Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = "Health Connect isn't available on this device.",
                modifier = Modifier.padding(16.dp),
            )
        }

        !uiState.hasPermissions -> Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
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

        else -> LazyColumn(modifier = modifier.fillMaxSize()) {
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
