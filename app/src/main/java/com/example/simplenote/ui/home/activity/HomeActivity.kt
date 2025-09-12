package com.example.simplenote.ui.home.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.simplenote.R
import com.example.simplenote.modules.note.common.model.Note
import com.example.simplenote.ui.home.viewmodel.HomeNavigationEvent
import com.example.simplenote.ui.home.viewmodel.HomeViewModel
import com.example.simplenote.ui.note.activity.NoteActivity
import com.example.simplenote.ui.search.activity.SearchActivity
import com.example.simplenote.ui.settings.activity.SettingsActivity


class HomeActivity : ComponentActivity() {
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val context = LocalContext.current
            val notes by viewModel.visibleNotes.collectAsState()
            val isLoading by viewModel.isLoading.collectAsState()
            val errorMessage by viewModel.errorMessage.collectAsState()
            val navigationEvent by viewModel.navigationEvent.collectAsState()

            LaunchedEffect(Unit) {
                viewModel.loadNotes(context)
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
                    is HomeNavigationEvent.ToSearch -> {
                        val intent = Intent(this@HomeActivity, SearchActivity::class.java)
                        intent.putParcelableArrayListExtra("notes", ArrayList(ev.notes))
                        startActivity(intent)
                        viewModel.onNavigationHandled()
                    }
                    HomeNavigationEvent.None -> Unit
                }
            }

            HomeScreen(
                notes = notes,
                isLoading = isLoading,
                errorMessage = errorMessage,
                onLoadMore = { viewModel.loadNextPage() },
                onSettingsClick = { viewModel.goToSettings() },
                onNoteClick = { id -> viewModel.goToNoteDetail(id) },
                onSearchClick = { viewModel.goToSearch() }
            )
        }
    }
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun HomeScreen(
        notes: List<Note>,
        isLoading: Boolean,
        errorMessage: String?,
        onLoadMore: () -> Unit,
        onSettingsClick: () -> Unit,
        onNoteClick: (Long?) -> Unit,
        onSearchClick: () -> Unit
    ) {
        val listState = rememberLazyListState()

        val shouldLoadMore by remember {
            derivedStateOf {
                val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                val total = listState.layoutInfo.totalItemsCount
                total > 0 && lastVisible >= total - 3
            }
        }
        LaunchedEffect(shouldLoadMore) {
            if (shouldLoadMore) onLoadMore()
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Home - ${notes.size} Notes") },
                    actions = {
                        IconButton(onClick = onSearchClick) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                        IconButton(onClick = onSettingsClick) {
                            Icon(Icons.Default.Settings, contentDescription = "Settings")
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { onNoteClick(null) },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Create Note",
                        tint = Color.White
                    )
                }
            }
        ) { padding ->
            Box(Modifier.padding(padding)) {
                when {
                    isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                    errorMessage != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(errorMessage, color = Color.Red)
                    }
                    notes.isEmpty() -> EmptyState()
                    else -> LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 88.dp)
                    ) {
                        items(notes) { note ->
                            NoteItem(note = note, onClick = { onNoteClick(note.id) })
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun EmptyState() {
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
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Start Your Journey",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Every big step starts with a small step. Note your first idea and start your journey!",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
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