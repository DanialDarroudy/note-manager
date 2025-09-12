package com.example.simplenote.ui.search.viewmodel

import androidx.lifecycle.ViewModel
import com.example.simplenote.modules.note.common.model.Note
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SearchViewModel: ViewModel() {
    private val _allNotes = mutableListOf<Note>()
    private var _searchResults = listOf<Note>()
    private val _visibleNotes = MutableStateFlow<List<Note>>(emptyList())
    val visibleNotes: StateFlow<List<Note>> get() = _visibleNotes
    private val _navigationEvent = MutableStateFlow<SearchNavigationEvent>(SearchNavigationEvent.None)
    val navigationEvent: StateFlow<SearchNavigationEvent> get() = _navigationEvent

    private var currentPage = 1
    private val pageSize = 10
    private var hasNext = false

    fun setNotes(notes: List<Note>) {
        _allNotes.clear()
        _allNotes.addAll(notes)
        _searchResults = _allNotes
        resetPagination()
    }

    fun search(query: String) {
        if (query.isBlank()) {
            _searchResults = _allNotes
        } else {
            val lowerQuery = query.lowercase()
            _searchResults = _allNotes.filter { it.title.lowercase().contains(lowerQuery) || it.description.lowercase().contains(lowerQuery) }
        }
        resetPagination()
    }

    private fun resetPagination() {
        currentPage = 1
        hasNext = _searchResults.size > pageSize
        _visibleNotes.value = _searchResults.take(pageSize)
    }

    fun loadNextPage() {
        if (!hasNext) return
        currentPage++
        val newSize = currentPage * pageSize
        _visibleNotes.value = _searchResults.take(newSize.coerceAtMost(_searchResults.size))
        hasNext = _visibleNotes.value.size < _searchResults.size
    }

    fun onNoteClick(id: Long) {
        _navigationEvent.value = SearchNavigationEvent.ToNote(id)
    }

    fun onBackToHomeClick() {
        _navigationEvent.value = SearchNavigationEvent.ToHome
    }

    fun onNavigationHandled() {
        _navigationEvent.value = SearchNavigationEvent.None
    }
}