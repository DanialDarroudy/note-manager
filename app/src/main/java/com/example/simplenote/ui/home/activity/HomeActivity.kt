package com.example.simplenote.ui.home.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.simplenote.R
import com.example.simplenote.modules.note.common.model.Note
import com.example.simplenote.ui.home.viewmodel.HomeNavigationEvent
import com.example.simplenote.ui.home.viewmodel.HomeViewModel
import com.example.simplenote.ui.note.activity.NoteActivity
import com.example.simplenote.ui.settings.activity.SettingsActivity


class HomeActivity : ComponentActivity() {
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MaterialTheme {
                val context = LocalContext.current
                val notes by viewModel.notes.collectAsState()
                val isInitialLoading by viewModel.isInitialLoading.collectAsState()
                val isPaging by viewModel.isPaging.collectAsState()
                val errorMessage by viewModel.errorMessage.collectAsState()
                val navigationEvent by viewModel.navigationEvent.collectAsState()
                val query by viewModel.query.collectAsState()

                LaunchedEffect(Unit) {
                    viewModel.loadFirstPage(context)
                }

                LaunchedEffect(navigationEvent) {
                    when (val ev = navigationEvent) {
                        is HomeNavigationEvent.ToSettings -> {
                            startActivity(Intent(this@HomeActivity, SettingsActivity::class.java))
                            viewModel.onNavigationHandled()
                        }
                        is HomeNavigationEvent.ToNote -> {
                            val intent = Intent(this@HomeActivity, NoteActivity::class.java)
                            intent.putExtra("id", ev.id)
                            startActivity(intent)
                            viewModel.onNavigationHandled()
                        }
                        HomeNavigationEvent.None -> Unit
                    }
                }

                HomeScreen(
                    notes = notes,
                    isInitialLoading = isInitialLoading,
                    isPaging = isPaging,
                    errorMessage = errorMessage,
                    query = query,
                    onQueryChange = { newQuery -> viewModel.updateQuery(newQuery, context) },
                    onSettingsClick = viewModel::goToSettings,
                    onCreateNoteClick = { viewModel.onNoteClick(null) },
                    onLoadMore = { viewModel.loadNextPage(context) },
                    onNoteClick = viewModel::onNoteClick
                )
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun HomeScreen(
        notes: List<Note>,
        isInitialLoading: Boolean,
        isPaging: Boolean,
        errorMessage: String?,
        query: String,
        onQueryChange: (String) -> Unit,
        onSettingsClick: () -> Unit,
        onCreateNoteClick: (Long?) -> Unit,
        onLoadMore: () -> Unit,
        onNoteClick: (Long?) -> Unit
    ) {
        val listState = rememberLazyListState()

        val shouldLoadMore by remember {
            derivedStateOf {
                val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                val total = listState.layoutInfo.totalItemsCount
                total > 0 && lastVisible >= total - 5
            }
        }
        LaunchedEffect(shouldLoadMore, isPaging, isInitialLoading, notes.size) {
            if (shouldLoadMore && !isPaging && !isInitialLoading) {
                onLoadMore()
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = "Home - ${notes.size} Notes") },
                    actions = {
                        IconButton(onClick = onSettingsClick) {
                            Icon(Icons.Default.Settings, contentDescription = "Settings")
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { onCreateNoteClick(null) },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Create Note", tint = Color.White)
                }
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                when {
                    isInitialLoading -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                    errorMessage != null -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(text = errorMessage, color = Color.Red)
                        }
                    }
                    notes.isEmpty() -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.emptynote),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(250.dp)
                            )
                            Text(
                                text = "Start Your Journey",
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = "Every big step starts with a small step. Note your first idea and start your journey!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                        }
                    }
                    else -> {
                        Column {
                            OutlinedTextField(
                                value = query,
                                onValueChange = onQueryChange,
                                label = { Text("Search notes") },
                                placeholder = { Text("Search on your notes: \"$query\"") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp)
                            )

                            LazyColumn(
                                state = listState,
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(bottom = 88.dp)
                            ) {
                                items(notes, key = { it.id }) { note ->
                                    NoteItem(note = note, onClick = { onNoteClick(note.id) })
                                }
                                if (isPaging) {
                                    item {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            CircularProgressIndicator()
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun NoteItem(note: Note, onClick: () -> Unit) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .clickable { onClick() }
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = note.title, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(4.dp))
                Text(
                    text = note.description.take(50) + if (note.description.length > 50) "..." else "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.DarkGray
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}