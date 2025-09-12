package com.example.simplenote.ui.note.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.simplenote.core.ui.redColor
import com.example.simplenote.ui.home.activity.HomeActivity
import com.example.simplenote.ui.note.viewmodel.NoteNavigationEvent
import com.example.simplenote.ui.note.viewmodel.NoteViewModel

class NoteActivity : ComponentActivity() {
    private val viewModel: NoteViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val noteId = intent.getLongExtra("id", -1).takeIf { it != -1L }

        setContent {
            val context = LocalContext.current
            val state by viewModel.uiState.collectAsState()
            val navigationEvent by viewModel.navigationEvent.collectAsState()

            LaunchedEffect(Unit) {
                viewModel.loadNote(context, noteId)
            }

            LaunchedEffect(navigationEvent) {
                when (navigationEvent) {
                    NoteNavigationEvent.ToHome -> {
                        startActivity(Intent(this@NoteActivity, HomeActivity::class.java))
                        finish()
                        viewModel.onNavigationHandled()
                    }

                    NoteNavigationEvent.None -> Unit
                }
            }

            NoteScreen(
                state = state,
                onBackClick = { viewModel.onBackClick(context) },
                onTitleChange = viewModel::onTitleChange,
                onDescriptionChange = viewModel::onDescriptionChange,
                onDeleteClick = viewModel::onDeleteClick,
                onDeleteCancel = viewModel::onDeleteCancel,
                onDeleteConfirm = { viewModel.onDeleteConfirm(context) }
            )
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun NoteScreen(
        state: NoteUiState,
        onBackClick: () -> Unit,
        onTitleChange: (String) -> Unit,
        onDescriptionChange: (String) -> Unit,
        onDeleteClick: () -> Unit,
        onDeleteCancel: () -> Unit,
        onDeleteConfirm: () -> Unit
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Note") },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            },
            bottomBar = {
                DeleteNoteBar(onDeleteClick = onDeleteClick)
            }
        ) { padding ->
            when {
                state.isLoading -> LoadingState(padding)
                state.errorMessage != null -> ErrorState(padding, state.errorMessage)
                else -> NoteContent(
                    title = state.title,
                    description = state.description,
                    updatedAt = state.updatedAt,
                    onTitleChange = onTitleChange,
                    onDescriptionChange = onDescriptionChange,
                    modifier = Modifier.padding(padding)
                )
            }
        }

        if (state.showDeleteDialog) {
            DeleteNoteDialog(
                onCancel = onDeleteCancel,
                onConfirm = onDeleteConfirm
            )
        }
    }

    @Composable
    fun LoadingState(padding: PaddingValues) {
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }

    @Composable
    fun ErrorState(padding: PaddingValues, message: String?) {
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(message ?: "", color = Color.Red)
        }
    }

    @Composable
    fun NoteContent(
        title: String,
        description: String,
        updatedAt: String,
        onTitleChange: (String) -> Unit,
        onDescriptionChange: (String) -> Unit,
        modifier: Modifier = Modifier
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = onTitleChange,
                placeholder = { Text("Title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = description,
                onValueChange = onDescriptionChange,
                placeholder = { Text("Feel Free to Write Here...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                maxLines = Int.MAX_VALUE
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Last edited on $updatedAt",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }

    @Composable
    fun DeleteNoteBar(onDeleteClick: () -> Unit) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(redColor)
                .clickable { onDeleteClick() }
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red)
                Spacer(Modifier.width(8.dp))
                Text("Delete Note", color = Color.Red)
            }
        }
    }

    @Composable
    fun DeleteNoteDialog(
        onCancel: () -> Unit,
        onConfirm: () -> Unit
    ) {
        AlertDialog(
            onDismissRequest = onCancel,
            title = { Text("Delete Note") },
            text = { Text("Are you sure you want to delete this note?") },
            confirmButton = {
                TextButton(onClick = onConfirm) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = onCancel) {
                    Text("Cancel")
                }
            }
        )
    }
}