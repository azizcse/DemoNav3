package com.tecfort.navigationtest.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * App-wide navigation drawer offering the Help and About destinations, wrapping [content].
 * Selecting an item does not close the drawer itself - callers are expected to do so
 * (typically after navigating) via [drawerState].
 */
@Composable
fun AppNavigationDrawer(
    drawerState: DrawerState,
    onHelpClick: () -> Unit,
    onAboutClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    ModalNavigationDrawer(
        modifier = modifier,
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                NavigationDrawerItem(
                    label = { Text("Help") },
                    selected = false,
                    onClick = onHelpClick,
                    modifier = Modifier.padding(horizontal = 12.dp),
                )
                NavigationDrawerItem(
                    label = { Text("About") },
                    selected = false,
                    onClick = onAboutClick,
                    modifier = Modifier.padding(horizontal = 12.dp),
                )
            }
        },
        content = content,
    )
}