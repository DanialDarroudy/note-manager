package com.example.simplenote.ui.home.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplenote.core.dependencyinjection.DependencyProvider
import com.example.simplenote.modules.note.common.model.Note
import com.example.simplenote.modules.note.getlist.abstraction.INoteListGetter
import com.example.simplenote.modules.note.getlist.dto.response.GetNoteListResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val noteListGetter: INoteListGetter = DependencyProvider.noteListGetter
    private val _navigationEvent = MutableStateFlow<HomeNavigationEvent>(HomeNavigationEvent.None)
    val navigationEvent: StateFlow<HomeNavigationEvent> get() = _navigationEvent
    private val _allNotes = mutableListOf<Note>()
    private val _visibleNotes = MutableStateFlow<List<Note>>(emptyList())
    val visibleNotes: StateFlow<List<Note>> get() = _visibleNotes
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> get() = _errorMessage

    private var currentPage = 1
    private val pageSize = 10

    fun loadNotes(context: Context) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val getNoteListResponse = noteListGetter.getNoteList(context)
            when (getNoteListResponse) {
                is GetNoteListResponse.Success -> {
                    _allNotes.clear()
                    _allNotes.addAll(getNoteListResponse.getNoteListResponseDto.notes)
                    currentPage = 1
                    _visibleNotes.value = _allNotes.take(pageSize)
                }
                is GetNoteListResponse.Failure -> {
                    _errorMessage.value = getNoteListResponse.exception.errors.firstOrNull()?.detail ?: "Failed to load notes"
                }
            }
            _isLoading.value = false
        }
    }

    fun loadNextPage() {
        if (_isLoading.value) return
        val nextIndex = currentPage * pageSize
        if (nextIndex >= _allNotes.size) return

        currentPage++
        val newSize = currentPage * pageSize
        _visibleNotes.value = _allNotes.take(newSize.coerceAtMost(_allNotes.size))
    }

    fun goToSettings() {
        _navigationEvent.value = HomeNavigationEvent.ToSettings
    }

    fun goToNoteDetail(id: Long?) {
        _navigationEvent.value = HomeNavigationEvent.ToNote(id)
    }

    fun goToSearch() {
        _navigationEvent.value = HomeNavigationEvent.ToSearch(_allNotes.toList())
    }

    fun onNavigationHandled() {
        _navigationEvent.value = HomeNavigationEvent.None
    }
}
