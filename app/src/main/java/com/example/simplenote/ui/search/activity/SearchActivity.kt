package com.example.simplenote.ui.search.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.simplenote.modules.note.common.model.Note
import com.example.simplenote.ui.note.activity.NoteActivity
import com.example.simplenote.ui.search.viewmodel.SearchNavigationEvent
import com.example.simplenote.ui.search.viewmodel.SearchViewModel

class SearchActivity : ComponentActivity() {
    private val viewModel: SearchViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val notes = intent.getParcelableArrayListExtra<Note>("notes") ?: arrayListOf()
        viewModel.setNotes(notes)

        setContent {
            val visibleNotes by viewModel.visibleNotes.collectAsState()
            val navigationEvent by viewModel.navigationEvent.collectAsState()

            LaunchedEffect(navigationEvent) {
                when (val ev = navigationEvent) {
                    is SearchNavigationEvent.ToNote -> {
                        startActivity(
                            Intent(
                                this@SearchActivity,
                                NoteActivity::class.java
                            ).putExtra("id", ev.id)
                        )
                        viewModel.onNavigationHandled()
                    }

                    SearchNavigationEvent.ToHome -> {
                        finish()
                        viewModel.onNavigationHandled()
                    }

                    SearchNavigationEvent.None -> Unit
                }
            }

            SearchScreen(
                notes = visibleNotes,
                onSearch = { query -> viewModel.search(query) },
                onLoadMore = { viewModel.loadNextPage() },
                onNoteClick = { id -> viewModel.onNoteClick(id) },
                onBackClick = { viewModel.onBackToHomeClick() }
            )
        }
    }


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun SearchScreen(
        notes: List<Note>,
        onSearch: (String) -> Unit,
        onLoadMore: () -> Unit,
        onNoteClick: (Long) -> Unit,
        onBackClick: () -> Unit
    ) {
        var query by remember { mutableStateOf("") }
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
                    title = {
                        OutlinedTextField(
                            value = query,
                            onValueChange = { query = it },
                            placeholder = { Text("Search notes") },
                            singleLine = true,
                            trailingIcon = {
                                IconButton(
                                    onClick = { onSearch(query) },
                                    modifier = Modifier.padding(end = 4.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Search,
                                        contentDescription = "Search"
                                    )
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                )
            },
            bottomBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = onBackClick,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Back to Home page")
                    }
                }
                Spacer(Modifier.height(16.dp))
            }
        ) { padding ->
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                items(notes) { note ->
                    NoteItem(note = note, onClick = { onNoteClick(note.id) })
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
}