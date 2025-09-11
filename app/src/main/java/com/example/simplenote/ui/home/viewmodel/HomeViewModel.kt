package com.example.simplenote.ui.home.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplenote.core.dependencyinjection.DependencyProvider
import com.example.simplenote.modules.note.common.model.Note
import com.example.simplenote.modules.note.common.model.NotesResponse
import com.example.simplenote.modules.note.getfilteredlist.abstraction.IFilteredNoteListGetter
import com.example.simplenote.modules.note.getfilteredlist.dto.request.GetFilteredNoteListRequestDto
import com.example.simplenote.modules.note.getlist.abstraction.INoteListGetter
import com.example.simplenote.modules.note.getlist.dto.request.GetNoteListRequestDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val noteListGetter: INoteListGetter = DependencyProvider.noteListGetter
    private val filteredNoteListGetter: IFilteredNoteListGetter = DependencyProvider.filteredNoteListGetter
    private val _navigationEvent = MutableStateFlow<HomeNavigationEvent>(HomeNavigationEvent.None)
    val navigationEvent: StateFlow<HomeNavigationEvent> get() = _navigationEvent
    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> get() = _notes
    private val _isInitialLoading = MutableStateFlow(false)
    val isInitialLoading: StateFlow<Boolean> get() = _isInitialLoading
    private val _isPaging = MutableStateFlow(false)
    val isPaging: StateFlow<Boolean> get() = _isPaging
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> get() = _errorMessage
    private val _query = MutableStateFlow("")
    val query: StateFlow<String> get() = _query
    private var currentPage = 1
    private val pageSize = 8
    private var hasNext = true
    fun updateQuery(query: String, context: Context) {
        _query.value = query
        loadFirstPage(context)
    }

    fun loadFirstPage(context: Context) {
        if (_isInitialLoading.value) return
        _isInitialLoading.value = true
        _errorMessage.value = null
        currentPage = 1
        hasNext = true
        _notes.value = emptyList()

        viewModelScope.launch {
            val response = if (_query.value.isBlank()) {
                val getNoteListRequestDto = GetNoteListRequestDto(page = currentPage, pageSize = pageSize)
                noteListGetter.getNoteList(getNoteListRequestDto, context)
            } else {
                val getFilteredNoteListRequestDto = GetFilteredNoteListRequestDto(
                    page = currentPage,
                    pageSize = pageSize,
                    title = _query.value,
                    description = _query.value
                )
                filteredNoteListGetter.getFilteredNoteList(getFilteredNoteListRequestDto, context)
            }

            when (response) {
                is NotesResponse.Success -> {
                    hasNext = response.notes.nextPage != null && response.notes.notes.isNotEmpty()
                    _notes.value = response.notes.notes
                }
                is NotesResponse.Failure -> {
                    _errorMessage.value = response.exception.errors.firstOrNull()?.detail ?: "Failed to load notes"
                }
            }
            _isInitialLoading.value = false
        }
    }

    fun loadNextPage(context: Context) {
        if (_isInitialLoading.value || _isPaging.value || !hasNext) return
        _isPaging.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            val nextPage = currentPage + 1
            val response = if (_query.value.isBlank()) {
                val getNoteListRequestDto = GetNoteListRequestDto(page = nextPage, pageSize = pageSize)
                noteListGetter.getNoteList(getNoteListRequestDto, context)
            } else {
                val getFilteredNoteListRequestDto = GetFilteredNoteListRequestDto(
                    page = nextPage,
                    pageSize = pageSize,
                    title = _query.value,
                    description = _query.value
                )
                filteredNoteListGetter.getFilteredNoteList(getFilteredNoteListRequestDto, context)
            }

            when (response) {
                is NotesResponse.Success -> {
                    val newNotes = response.notes.notes
                    hasNext = response.notes.nextPage != null && newNotes.isNotEmpty()
                    if (newNotes.isNotEmpty()) {
                        currentPage = nextPage
                        _notes.value = _notes.value + newNotes
                    }
                }
                is NotesResponse.Failure -> {
                    _errorMessage.value = response.exception.errors.firstOrNull()?.detail ?: "Failed to load more notes"
                }
            }
            _isPaging.value = false
        }
    }

    fun onNoteClick(id: Long?) {
        _navigationEvent.value = HomeNavigationEvent.ToNote(id)
    }

    fun goToSettings() {
        _navigationEvent.value = HomeNavigationEvent.ToSettings
    }

    fun onNavigationHandled() {
        _navigationEvent.value = HomeNavigationEvent.None
    }
}
