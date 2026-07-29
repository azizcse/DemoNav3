package com.tecfort.navigationtest.ui.profilesetting

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var displayName by rememberSaveable(initialDisplayName) { mutableStateOf(initialDisplayName) }

    Scaffold(
        modifier = modifier,
        topBar = { AppTopBar(title = "Profile Setting", onBackClick = onBackClick) },
    ) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            OutlinedTextField(
                value = displayName,
                onValueChange = { displayName = it },
                label = { Text("Display name") },
                modifier = Modifier.fillMaxWidth(),
            )
            Button(onClick = { onSave(displayName) }) { Text("Save and go back") }
        }
    }
}
