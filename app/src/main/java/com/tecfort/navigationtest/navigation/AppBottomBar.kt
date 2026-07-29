package com.tecfort.navigationtest.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Bottom navigation bar over the app's [TopLevelDestination] tabs.
 */
@Composable
fun AppBottomBar(
    selected: TopLevelDestination?,
    onTabClick: (TopLevelDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavigationBar(modifier = modifier) {
        TopLevelDestination.entries.forEach { top ->
            NavigationBarItem(
                selected = top == selected,
                onClick = { onTabClick(top) },
                icon = { Icon(top.icon, contentDescription = null) },
                label = { Text(top.label) },
            )
        }
    }
}
