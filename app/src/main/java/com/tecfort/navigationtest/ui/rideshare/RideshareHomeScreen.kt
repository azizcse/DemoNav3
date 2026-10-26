package com.tecfort.navigationtest.ui.rideshare

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Rideshare landing screen designed from Figma node 44574-95789
 * (Foodi / Rideshare 2026). Composed from top-nav + search, a horizontal
 * services strip, and three two-card promo bands.
 */
@Composable
fun RideshareHomeScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        LazyColumn(contentPadding = PaddingValues(bottom = 24.dp)) {
            item { TopNavBar(onBackClick = onBackClick) }
            item { SearchAndSavedAddresses() }
            item { Spacer(Modifier.height(8.dp)) }
            item { ServicesStrip() }
            item { Spacer(Modifier.height(8.dp)) }
            item { BannerSection(title = "Save every day", cards = SAVE_CARDS) }
            item { BannerSection(title = "Elevate your ride", cards = ELEVATE_CARDS) }
            item { BannerSection(title = "Keep every ride safe", cards = SAFE_CARDS) }
        }
    }
}

@Composable
private fun TopNavBar(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.size(48.dp),
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
            )
        }
        Spacer(Modifier.weight(1f))
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Default.Map,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Map",
                style = MaterialTheme.typography.titleSmall,
            )
        }
    }
}

@Composable
private fun SearchAndSavedAddresses() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Where to?",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f),
            )
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "Later",
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }
        SavedAddressRow(
            icon = Icons.Default.Home,
            title = "Home",
            subtitle = "Harez road, Nadda, Vatara",
        )
        SavedAddressRow(
            icon = Icons.Default.History,
            title = "Dhaka district, Dhaka",
            subtitle = null,
        )
    }
}

@Composable
private fun SavedAddressRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String?,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
            )
        }
        Spacer(Modifier.width(8.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

private data class ServiceItem(val label: String, val tint: Color)

private val SERVICES = listOf(
    ServiceItem("Bike", Color(0xFFFFE0B2)),
    ServiceItem("Car", Color(0xFFFFCCBC)),
    ServiceItem("Ride", Color(0xFFFFECB3)),
    ServiceItem("Rental", Color(0xFFC8E6C9)),
    ServiceItem("Parcel", Color(0xFFB3E5FC)),
    ServiceItem("Mart", Color(0xFFD1C4E9)),
    ServiceItem("CNG", Color(0xFFF8BBD0)),
)

@Composable
private fun ServicesStrip() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        SERVICES.forEach { ServiceCell(it) }
    }
}

@Composable
private fun ServiceCell(item: ServiceItem) {
    Column(
        modifier = Modifier.width(80.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(width = 56.dp, height = 42.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(item.tint),
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = item.label,
            style = MaterialTheme.typography.labelSmall,
        )
    }
}

private data class BannerCard(val heading: String, val paragraph: String, val tint: Color)

private val SAVE_CARDS = listOf(
    BannerCard("Bike rides", "Move through traffic & save time", Color(0xFFBBDEFB)),
    BannerCard("CNG rides", "Ideal for daily city commutes", Color(0xFFC5CAE9)),
)
private val ELEVATE_CARDS = listOf(
    BannerCard("Intercity", "Seamless travel across cities", Color(0xFFB2DFDB)),
    BannerCard("Book Rentals", "Rentals for every occasion", Color(0xFFDCEDC8)),
)
private val SAFE_CARDS = listOf(
    BannerCard("Share your ride", "Let loved one track your ride", Color(0xFFFFF9C4)),
    BannerCard("Safety Coverage", "Added protection for every ride", Color(0xFFFFCCBC)),
)

@Composable
private fun BannerSection(title: String, cards: List<BannerCard>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp, fontWeight = FontWeight.SemiBold),
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            cards.forEach { BannerCardView(it) }
        }
    }
}

@Composable
private fun BannerCardView(card: BannerCard) {
    Column(modifier = Modifier.width(224.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(124.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(card.tint),
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = card.heading,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
        )
        Text(
            text = card.paragraph,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
