package com.tecfort.navigationtest.ui.profilesetting

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tecfort.navigationtest.ui.components.AppTopBar

/**
 * Lets the user edit their display name, then hands the new value back to whoever pushed this
 * screen via [onSave]. Nav 3 has no built-in "result" channel (unlike Navigation-Compose's
 * `SavedStateHandle`), so the value simply flows back through this callback - see
 * [com.tecfort.navigationtest.navigation.NavigationRoot], which owns the state SettingScreen
 * reads and passes it into [onSave].
 */
@Composable
fun ProfileSettingScreen(
    initialDisplayName: String,
    onSave: (displayName: String) -> Unit,
    onChangePasswordClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var displayName by rememberSaveable(initialDisplayName) { mutableStateOf(initialDisplayName) }

    Scaffold(
        modifier = modifier,
        topBar = { AppTopBar(title = "Profile Setting", onBackClick = onBackClick) },
    ) { innerPadding ->
        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item { CategoryGrid(items = sampleItems) }
        }
    }
}

data class Item(val name: String, val icon: ImageVector)

val sampleItems = listOf(
    Item("Car", Icons.Default.Home),
    Item("Bike", Icons.Default.Favorite),
    Item("CNG", Icons.Default.Star),
    Item("Intercity", Icons.Default.ShoppingCart),
    Item("Schedule", Icons.Default.Email),
    Item("Car Hire", Icons.Default.Info),
    Item("Shuttle", Icons.Default.Person),
    Item("Fish", Icons.Default.Search),
    Item("Frozen", Icons.Default.Lock),
    Item("Organic", Icons.Default.Place),
    Item("Spices", Icons.Default.Settings),
    Item("Sweets", Icons.Default.Call),
)

private const val COLUMNS = 4
private const val MAX_GRID_ITEMS = 8
private val ROW_CARD_WIDTH = 80.dp
private val CardBackground = Color(0xFFF6F7F8)

/**
 * Fewer than 8 items: a single horizontally scrollable row.
 * Exactly 8 items: 4-column grid.
 * More than 8: the first 7 items plus a "More" cell that opens a bottom sheet with every item.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryGrid(items: List<Item>, modifier: Modifier = Modifier) {
    var showSheet by rememberSaveable { mutableStateOf(false) }

    when {
        items.isEmpty() -> Unit
        items.size < MAX_GRID_ITEMS -> Row(
            modifier = modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items.forEach { ItemCard(it, Modifier.width(ROW_CARD_WIDTH)) }
        }

        else -> {
            val hasMore = items.size > MAX_GRID_ITEMS
            val cells: List<Item?> =
                if (hasMore) items.take(MAX_GRID_ITEMS - 1) + null else items
            Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                cells.chunked(COLUMNS).forEach { rowCells ->
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        rowCells.forEach { cell ->
                            if (cell == null) {
                                ItemCard(
                                    Item("More", Icons.Default.MoreVert),
                                    Modifier.weight(1f),
                                    onClick = { showSheet = true },
                                )
                            } else {
                                ItemCard(cell, Modifier.weight(1f))
                            }
                        }
                        // Keep cell width consistent on a partial last row.
                        repeat(COLUMNS - rowCells.size) { Spacer(Modifier.weight(1f)) }
                    }
                }
            }
        }
    }

    if (showSheet) {
        ModalBottomSheet(onDismissRequest = { showSheet = false }) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(COLUMNS),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(items) { ItemCard(it) }
            }
        }
    }
}

@Composable
private fun ItemCard(item: Item, modifier: Modifier = Modifier, onClick: (() -> Unit)? = null) {
    val content: @Composable ColumnScope.() -> Unit = {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(item.icon, contentDescription = null)
            Text(item.name, style = MaterialTheme.typography.labelMedium, maxLines = 1)
        }
    }
    val colors = CardDefaults.cardColors(containerColor = CardBackground)
    if (onClick != null) {
        Card(onClick = onClick, modifier = modifier, colors = colors, content = content)
    } else {
        Card(modifier = modifier, colors = colors, content = content)
    }
}

@Preview(showBackground = true, name = "1 item")
@Composable
private fun PreviewOneItem() = CategoryGrid(sampleItems.take(1), Modifier.padding(16.dp))

@Preview(showBackground = true, name = "2 items")
@Composable
private fun PreviewTwoItems() = CategoryGrid(sampleItems.take(2), Modifier.padding(16.dp))

@Preview(showBackground = true, name = "3 items")
@Composable
private fun PreviewThreeItems() = CategoryGrid(sampleItems.take(3), Modifier.padding(16.dp))

@Preview(showBackground = true, name = "5 items")
@Composable
private fun PreviewFiveItems() = CategoryGrid(sampleItems.take(5), Modifier.padding(16.dp))

@Preview(showBackground = true, name = "Exactly 8 items (no More)")
@Composable
private fun PreviewEightItems() = CategoryGrid(sampleItems.take(8), Modifier.padding(16.dp))

@Preview(showBackground = true, name = "12 items (More cell)")
@Composable
private fun PreviewTwelveItems() = CategoryGrid(sampleItems, Modifier.padding(16.dp))


