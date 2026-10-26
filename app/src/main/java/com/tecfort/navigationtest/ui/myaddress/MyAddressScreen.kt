package com.tecfort.navigationtest.ui.myaddress

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Work
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * My Address screen designed from Figma node 45068-194130
 * (Foodi / Rideshare 2026). Lists saved addresses with a prominent
 * "Add new address" entry and per-row more actions.
 */

private val BrandRed = Color(0xFFE10101)
private val BgTertiary = Color(0xFFF6F7F8)
private val BorderPrimary = Color(0xFFE7ECEF)
private val BorderDisabled = Color(0xFFF3F3F3)
private val TextPrimary = Color(0xFF1B1D22)
private val TextSecondary = Color(0xFF51555C)

@Composable
fun MyAddressScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color.White,
    ) {
        LazyColumn(contentPadding = PaddingValues(bottom = 24.dp)) {
            item { TopNav(onBackClick = onBackClick) }
            item {
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                    AddNewAddressButton(onClick = { })
                }
            }
            item {
                SavedAddressesSection(addresses = SAVED_ADDRESSES)
            }
        }
    }
}

@Composable
private fun TopNav(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .border(1.dp, BorderDisabled, CircleShape)
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
        Text(
            text = "My Addresses",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                fontSize = 20.sp,
            ),
        )
    }
}

@Composable
private fun AddNewAddressButton(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(50))
            .border(1.dp, BorderPrimary, RoundedCornerShape(50))
            .background(Color.White)
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = null,
            tint = BrandRed,
            modifier = Modifier.size(24.dp),
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
    SavedAddress(
        Icons.Default.Work,
        "Work",
        "Rahman’s Regnum Centre, 191/B Bir Uttam Mir Shawkat Sarak, Dhaka 1208",
    ),
    SavedAddress(Icons.Default.Star, "Mirpur", "Mirpur DOHS Gate No.1 , Mirpur DOHS"),
)

@Composable
private fun SavedAddressesSection(addresses: List<SavedAddress>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "Saved Addresses",
            modifier = Modifier.padding(vertical = 16.dp),
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Medium,
                color = TextPrimary,
                fontSize = 16.sp,
            ),
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            addresses.forEachIndexed { index, address ->
                SavedAddressRow(address)
                if (index < addresses.lastIndex) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(BorderPrimary),
                    )
                }
            }
        }
    }
}

@Composable
private fun SavedAddressRow(address: SavedAddress) {
    Row(
        modifier = Modifier.fillMaxWidth(),
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
            tint = TextSecondary,
        )
    }
}
