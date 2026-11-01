package com.tecfort.navigationtest.ui.orderdetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailsScreen(
    orderId: String,
    restaurantName: String,
    pickupAddress: String,
    dropoffAddress: String,
    distanceKm: Double,
    payout: Double,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Order Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        ) {
            SummaryCard(
                orderId = orderId,
                restaurantName = restaurantName,
                payout = payout,
                distanceKm = distanceKm,
            )
            Spacer(Modifier.height(16.dp))
            RouteCard(
                pickupAddress = pickupAddress,
                dropoffAddress = dropoffAddress,
            )
            Spacer(Modifier.height(16.dp))
            CustomerCard()
            Spacer(Modifier.height(16.dp))
            ItemsCard()
            Spacer(Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedButton(
                    onClick = onBackClick,
                    modifier = Modifier.weight(1f),
                ) { Text("Close") }
            }
        }
    }
}

@Composable
private fun SummaryCard(
    orderId: String,
    restaurantName: String,
    payout: Double,
    distanceKm: Double,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = "Order #$orderId",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = restaurantName,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                Stat(label = "Payout", value = "৳%.0f".format(payout))
                Stat(label = "Distance", value = "%.1f km".format(distanceKm))
                Stat(label = "ETA", value = "18 min")
            }
        }
    }
}

@Composable
private fun Stat(label: String, value: String) {
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall)
        Text(value, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
private fun RouteCard(pickupAddress: String, dropoffAddress: String) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text("Route", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(12.dp))
            LabeledLine(label = "Pickup", value = pickupAddress)
            Spacer(Modifier.height(8.dp))
            LabeledLine(label = "Drop-off", value = dropoffAddress)
        }
    }
}

@Composable
private fun CustomerCard() {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text("Customer", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(12.dp))
            LabeledLine(label = "Name", value = "Rafiq Ahmed")
            Spacer(Modifier.height(6.dp))
            LabeledLine(label = "Phone", value = "+880 1700-123456")
            Spacer(Modifier.height(6.dp))
            LabeledLine(label = "Payment", value = "Cash on Delivery")
        }
    }
}

@Composable
private fun ItemsCard() {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text("Items (3)", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(12.dp))
            OrderItemRow(name = "Beef Burger", qty = 2, price = 520.0)
            OrderItemRow(name = "French Fries (L)", qty = 1, price = 160.0)
            OrderItemRow(name = "Coke 500ml", qty = 2, price = 120.0)
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("Total", style = MaterialTheme.typography.titleMedium)
                Text("৳800", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Composable
private fun OrderItemRow(name: String, qty: Int, price: Double) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text("$qty× $name", style = MaterialTheme.typography.bodyMedium)
        Text("৳%.0f".format(price), style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun LabeledLine(label: String, value: String) {
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall)
        Text(value, style = MaterialTheme.typography.bodyLarge)
    }
}
