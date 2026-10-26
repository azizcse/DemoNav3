package com.tecfort.navigationtest.ui.transactionhistory

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Headset
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Transaction history screen designed from Figma node 44316-195751
 * (Foodi / Rideshare 2026). Shows a date-range filter, grouped-by-day
 * transactions with brand accent colors, and a filter bottom sheet.
 */

private val BrandRed = Color(0xFFE10101)
private val ErrorRed = Color(0xFFFF3A2C)
private val SuccessGreen = Color(0xFF079455)
private val BgTertiary = Color(0xFFF6F7F8)
private val BorderPrimary = Color(0xFFE7ECEF)
private val TextPrimary = Color(0xFF1B1D22)
private val TextSecondary = Color(0xFF51555C)
private val TextPlaceholder = Color(0xFFB8B9BA)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionHistoryScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showFilter by remember { mutableStateOf(false) }
    var selectedRange by remember { mutableStateOf(DateRange.LAST_7_DAYS) }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color.White,
    ) {
        LazyColumn(contentPadding = PaddingValues(bottom = 24.dp)) {
            item { TopNav(onBackClick = onBackClick, onFilterClick = { showFilter = true }) }
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    RangeTabsRow(
                        selected = selectedRange,
                        onSelect = { selectedRange = it },
                    )
                    PeriodLabel(range = "Sep 12 - Sep 18")
                }
            }
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(1.dp)
                        .background(BorderPrimary),
                )
            }
            transactionGroups(TRANSACTION_GROUPS)
        }

        if (showFilter) {
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ModalBottomSheet(
                onDismissRequest = { showFilter = false },
                sheetState = sheetState,
            ) {
                FilterSheetContent(onDone = { showFilter = false })
            }
        }
    }
}

@Composable
private fun TopNav(onBackClick: () -> Unit, onFilterClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().background(Color.White)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
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
            Text(
                text = "Transaction history",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = TextPrimary,
                ),
                modifier = Modifier.weight(1f),
            )
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .border(1.dp, BorderPrimary, CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center,
            ) {
                IconButton(onClick = onFilterClick) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Filter",
                        tint = TextPrimary,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(BorderPrimary),
        )
    }
}

private enum class DateRange(val label: String) {
    LAST_7_DAYS("Last 7 days"),
    LAST_30_DAYS("Last 30 days"),
    CUSTOM("Custom"),
}

@Composable
private fun RangeTabsRow(selected: DateRange, onSelect: (DateRange) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        DateRange.entries.forEach { range ->
            RangePill(
                range = range,
                selected = range == selected,
                onClick = { onSelect(range) },
            )
        }
    }
}

@Composable
private fun RangePill(range: DateRange, selected: Boolean, onClick: () -> Unit) {
    val bg = if (selected) BrandRed else Color.White
    val textColor = if (selected) Color.White else TextSecondary
    val border = if (selected) null else BorderPrimary
    Row(
        modifier = Modifier
            .height(36.dp)
            .clip(RoundedCornerShape(50))
            .then(if (border != null) Modifier.border(1.dp, border, RoundedCornerShape(50)) else Modifier)
            .background(bg)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        if (range == DateRange.CUSTOM) {
            Icon(
                imageVector = Icons.Default.Tune,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = textColor,
            )
        }
        Text(
            text = range.label,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Medium,
                color = textColor,
                fontSize = 14.sp,
            ),
            modifier = Modifier.padding(start = 0.dp),
        )
    }
}

@Composable
private fun PeriodLabel(range: String) {
    val annotated = buildAnnotatedString {
        withStyle(SpanStyle(color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Medium)) {
            append("Showing usage from ")
        }
        withStyle(SpanStyle(color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Medium)) {
            append("“$range”")
        }
    }
    Text(text = annotated)
}

private data class Transaction(
    val icon: ImageVector,
    val title: String,
    val subtitle: String?,
    val timestamp: String,
    val amount: String,
    val amountColor: Color,
)

private data class TransactionGroup(val date: String, val items: List<Transaction>)

private val TRANSACTION_GROUPS = listOf(
    TransactionGroup(
        date = "Sep 22, 2026",
        items = listOf(
            Transaction(Icons.Default.DirectionsCar, "Cancellation fee", "Car premium", "May 17, 2026, 4:30 PM", "TK 50", ErrorRed),
            Transaction(Icons.Default.Headset, "Customer support", null, "May 12, 2026, 6:45 PM", "TK 120", SuccessGreen),
        ),
    ),
    TransactionGroup(
        date = "Sep 21, 2026",
        items = listOf(
            Transaction(Icons.Default.DirectionsBike, "Waiting fee", "Bike", "May 10, 2026, 10:30 AM", "TK 20", ErrorRed),
        ),
    ),
    TransactionGroup(
        date = "Sep 20, 2026",
        items = listOf(
            Transaction(Icons.Default.DirectionsBike, "Cancellation fee", "Bike", "10:30 AM", "TK 20", ErrorRed),
        ),
    ),
    TransactionGroup(
        date = "Sep 15, 2026",
        items = listOf(
            Transaction(Icons.Default.AttachMoney, "Subscription", null, "12:59 PM", "TK 150", ErrorRed),
        ),
    ),
)

private fun LazyListScope.transactionGroups(groups: List<TransactionGroup>) {
    groups.forEach { group ->
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    text = group.date,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontSize = 16.sp,
                    ),
                )
                group.items.forEachIndexed { index, tx ->
                    TransactionRow(tx)
                    if (index < group.items.lastIndex) {
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
}

@Composable
private fun TransactionRow(tx: Transaction) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(BgTertiary),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = tx.icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = TextPrimary,
            )
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = tx.title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary,
                        fontSize = 16.sp,
                    ),
                )
                if (tx.subtitle != null) {
                    Text(
                        text = "•",
                        color = TextPlaceholder,
                        fontSize = 10.sp,
                    )
                    Text(
                        text = tx.subtitle,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary,
                            fontSize = 16.sp,
                        ),
                    )
                }
            }
            Text(
                text = tx.timestamp,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextSecondary,
                    fontSize = 12.sp,
                ),
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = tx.amount,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = tx.amountColor,
                    fontSize = 14.sp,
                ),
            )
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = TextSecondary,
            )
        }
    }
}

@Composable
private fun FilterSheetContent(onDone: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        Text(
            text = "Filter",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                fontSize = 20.sp,
            ),
        )
        FilterGroup(title = "Service", options = listOf("All", "Food", "FoodiGo", "Mart", "Parcel"))
        FilterGroup(title = "Transaction type", options = listOf("All", "Spent", "Received"))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(50))
                .background(BrandRed)
                .clickable(onClick = onDone)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "Done",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 16.sp,
                ),
            )
        }
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun FilterGroup(title: String, options: List<String>) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Medium,
                color = TextPrimary,
                fontSize = 16.sp,
            ),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            options.forEachIndexed { index, option ->
                FilterOptionPill(label = option, selected = index == 0)
            }
        }
    }
}

@Composable
private fun FilterOptionPill(label: String, selected: Boolean) {
    val textColor = if (selected) BrandRed else TextSecondary
    val modifier = if (selected) {
        Modifier
            .height(36.dp)
            .clip(RoundedCornerShape(50))
            .border(1.dp, BorderPrimary, RoundedCornerShape(50))
            .background(Color.White)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    } else {
        Modifier
            .height(36.dp)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    }
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Medium,
                color = textColor,
                fontSize = 14.sp,
            ),
        )
    }
}

