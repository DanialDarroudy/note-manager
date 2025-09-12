package com.example.simplenote.ui.settings.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.simplenote.R
import com.example.simplenote.ui.settings.viewmodel.SettingsNavigationEvent
import com.example.simplenote.ui.settings.viewmodel.SettingsViewModel
import com.example.simplenote.ui.start.activity.StartActivity


class SettingsActivity: ComponentActivity() {
    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val context = LocalContext.current
            val uiState = SettingsUiState(
                firstName = viewModel.firstName.collectAsState().value,
                lastName = viewModel.lastName.collectAsState().value,
                email = viewModel.email.collectAsState().value,
                isLoading = viewModel.isLoading.collectAsState().value,
                errorMessage = viewModel.errorMessage.collectAsState().value,
                showLogoutDialog = viewModel.showLogoutDialog.collectAsState().value
            )
            val navigationEvent by viewModel.navigationEvent.collectAsState()

            LaunchedEffect(Unit) {
                viewModel.loadUser(context)
            }

            LaunchedEffect(navigationEvent) {
                when (navigationEvent) {
                    SettingsNavigationEvent.ToHome -> {
                        finish()
                        viewModel.onNavigationHandled()
                    }
                    SettingsNavigationEvent.ToStart -> {
                        val intent = Intent(this@SettingsActivity, StartActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        viewModel.onNavigationHandled()
                    }
                    SettingsNavigationEvent.None -> Unit
                }
            }

            SettingsScreen(
                state = uiState,
                onBackClick = { viewModel.onBackClick() },
                onLogoutClick = { viewModel.onLogoutClick() },
                onLogoutCancel = { viewModel.onLogoutCancel() },
                onLogoutConfirm = { viewModel.onLogoutConfirm() }
            )
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun SettingsScreen(
        state: SettingsUiState,
        onBackClick: () -> Unit,
        onLogoutClick: () -> Unit,
        onLogoutCancel: () -> Unit,
        onLogoutConfirm: () -> Unit
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Settings") },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { padding ->
            when {
                state.isLoading -> Box(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
                state.errorMessage != null -> Box(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(state.errorMessage, color = Color.Red)
                }
                else -> SettingsContent(
                    firstName = state.firstName,
                    lastName = state.lastName,
                    email = state.email,
                    onLogoutClick = onLogoutClick,
                    modifier = Modifier.padding(padding)
                )
            }
        }

        if (state.showLogoutDialog) {
            AlertDialog(
                onDismissRequest = onLogoutCancel,
                title = { Text("Log Out") },
                text = { Text("Are you sure you want to log out from the application?") },
                confirmButton = {
                    TextButton(onClick = onLogoutConfirm) {
                        Text("Yes")
                    }
                },
                dismissButton = {
                    TextButton(onClick = onLogoutCancel) {
                        Text("Cancel")
                    }
                }
            )
        }
    }

    @Composable
    fun SettingsContent(
        firstName: String,
        lastName: String,
        email: String,
        onLogoutClick: () -> Unit,
        modifier: Modifier = Modifier
    ) {
        Column(modifier = modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.profile),
                    contentDescription = null,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                )
                Spacer(Modifier.width(16.dp))
                Column {
                    Text("$firstName $lastName", style = MaterialTheme.typography.titleMedium)
                    Text(email, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                }
            }

            Divider()

            ListItem(
                headlineContent = {
                    Text("Log Out", color = Color.Red)
                },
                leadingContent = {
                    Icon(
                        Icons.Default.ExitToApp,
                        contentDescription = null,
                        tint = Color.Red
                    )
                },
                modifier = Modifier.clickable { onLogoutClick() }
            )


            Spacer(modifier = Modifier.weight(1f))

            Text(
                "Danial Notes v1.1",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 16.dp)
            )
        }
    }
}