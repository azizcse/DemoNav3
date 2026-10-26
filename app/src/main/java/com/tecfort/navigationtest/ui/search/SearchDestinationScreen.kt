package com.tecfort.navigationtest.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * "Plan your ride" screen designed from Figma node 43495-133985
 * (Foodi / Rideshare 2026): a destination picker with pickup/dropoff form,
 * Recent/Saved tabs, add-address action, and a list of saved addresses.
 */

private val BrandRed = Color(0xFFE10101)
private val BgTertiary = Color(0xFFF6F7F8)
private val BorderPrimary = Color(0xFFE7ECEF)
private val TextPrimary = Color(0xFF1B1D22)
private val TextSecondary = Color(0xFF51555C)
private val TextTertiary = Color(0xFF6F7379)

@Composable
fun SearchDestinationScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color.White,
    ) {
        LazyColumn(contentPadding = PaddingValues(bottom = 24.dp)) {
            item { TopNav(onBackClick = onBackClick) }
            item { FilterRow() }
            item { PickDropForm() }
            item { TabsRow() }
            item { AddNewAddressButton() }
            item { Spacer(Modifier.height(8.dp)) }
            items(SAVED_ADDRESSES)
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.items(
    addresses: List<SavedAddress>,
) {
    addresses.forEachIndexed { index, address ->
        item {
            SavedAddressRow(address)
            if (index < addresses.lastIndex) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(1.dp)
                        .background(BorderPrimary),
                )
            }
        }
    }
}

@Composable
private fun TopNav(onBackClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .border(1.dp, BorderPrimary, CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center,
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary,
                    )
                }
            }
            Spacer(Modifier.width(12.dp))
            Text(
                text = "Plan your ride",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = TextPrimary,
                ),
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(BorderPrimary),
        )
    }
}

@Composable
private fun FilterRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        FilterChip(icon = Icons.Default.Schedule, text = "Pick-up now")
        FilterChip(icon = Icons.Default.Person, text = "For me")
    }
}

@Composable
private fun FilterChip(icon: ImageVector, text: String) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(BgTertiary)
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = TextPrimary,
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary,
                    fontSize = 14.sp,
                ),
            )
            Icon(
                imageVector = Icons.Default.ExpandMore,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = TextPrimary,
            )
        }
    }
}

@Composable
private fun PickDropForm() {
    var pickupFocused by remember { mutableStateOf(false) }
    var dropoffFocused by remember { mutableStateOf(false) }
    var pickupText by remember { mutableStateOf("Rahman's Regnum Center") }
    var dropoffText by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(BgTertiary)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.RadioButtonChecked,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = TextPrimary,
                )
                BasicTextField(
                    value = pickupText,
                    onValueChange = { pickupText = it },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary),
                    cursorBrush = SolidColor(BrandRed),
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (pickupFocused) Color.White else BgTertiary)
                        .onFocusChanged { pickupFocused = it.isFocused }
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = BrandRed,
                )
                BasicTextField(
                    value = dropoffText,
                    onValueChange = { dropoffText = it },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary),
                    cursorBrush = SolidColor(BrandRed),
                    decorationBox = { innerTextField ->
                        if (dropoffText.isEmpty()) {
                            Text(
                                text = "Drop-off",
                                style = MaterialTheme.typography.bodyMedium.copy(color = TextTertiary),
                            )
                        }
                        innerTextField()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (dropoffFocused) Color.White else BgTertiary)
                        .onFocusChanged { dropoffFocused = it.isFocused }
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                )
            }
        }
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .border(1.dp, BorderPrimary, CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add stop",
                modifier = Modifier.size(16.dp),
                tint = TextPrimary,
            )
        }
    }
}

@Composable
private fun TabsRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Recent",
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Medium,
                color = TextSecondary,
                fontSize = 14.sp,
            ),
        )
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .border(1.dp, BorderPrimary, RoundedCornerShape(50))
                .background(Color.White)
                .padding(horizontal = 12.dp, vertical = 8.dp),
        ) {
            Text(
                text = "Saved",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Medium,
                    color = BrandRed,
                    fontSize = 14.sp,
                ),
            )
        }
    }
}

@Composable
private fun AddNewAddressButton() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(50))
            .border(1.dp, BorderPrimary, RoundedCornerShape(50))
            .background(Color.White)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = BrandRed,
        )
        Text(
            text = "Add new address",
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                color = BrandRed,
                fontSize = 16.sp,
            ),
        )
    }
}

private data class SavedAddress(
    val icon: ImageVector,
    val title: String,
    val subtitle: String,
)

private val SAVED_ADDRESSES = listOf(
    SavedAddress(Icons.Default.Home, "Home", "Mirpur DOHS Gate No.1 , Mirpur DOHS"),
    SavedAddress(Icons.Default.Star, "Bashundhara", "Bashundhara Shopping mall, Bashundhara"),
    SavedAddress(Icons.Default.Work, "Office", "Rahman's Regnum Centre, 191/B Bir Uttam Mir Shawkat Sarak, Dhaka 1208"),
    SavedAddress(Icons.Default.Star, "Mirpur", "Mirpur DOHS Gate No.1 , Mirpur DOHS"),
)

@Composable
private fun SavedAddressRow(address: SavedAddress) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(BgTertiary),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = address.icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = TextPrimary,
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = address.title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    fontSize = 16.sp,
                ),
            )
            Text(
                text = address.subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Medium,
                    color = TextSecondary,
                    fontSize = 12.sp,
                ),
            )
        }
        Icon(
            imageVector = Icons.Default.MoreVert,
            contentDescription = "More",
            modifier = Modifier.size(20.dp),
            tint = TextPrimary,
        )
    }
}
