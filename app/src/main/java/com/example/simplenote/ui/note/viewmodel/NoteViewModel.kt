package com.example.simplenote.ui.note.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplenote.core.dependencyinjection.DependencyProvider
import com.example.simplenote.modules.note.common.model.NoteResponse
import com.example.simplenote.modules.note.create.abstraction.INoteCreator
import com.example.simplenote.modules.note.create.dto.request.CreateNoteRequestDto
import com.example.simplenote.modules.note.destroy.abstraction.INoteDestroyer
import com.example.simplenote.modules.note.destroy.dto.request.DestroyNoteRequestDto
import com.example.simplenote.modules.note.retrieve.abstraction.INoteRetriever
import com.example.simplenote.modules.note.retrieve.dto.request.RetrieveNoteRequestDto
import com.example.simplenote.modules.note.update.abstraction.INoteUpdater
import com.example.simplenote.modules.note.update.dto.request.UpdateNoteRequestDto
import com.example.simplenote.ui.note.activity.NoteUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NoteViewModel: ViewModel() {
    private val noteCreator: INoteCreator = DependencyProvider.noteCreator
    private val noteRetriever: INoteRetriever = DependencyProvider.noteRetriever
    private val noteUpdater: INoteUpdater = DependencyProvider.noteUpdater
    private val noteDestroyer: INoteDestroyer = DependencyProvider.noteDestroyer
    private val _uiState = MutableStateFlow(NoteUiState())
    val uiState: StateFlow<NoteUiState> get() = _uiState

    private val _navigationEvent = MutableStateFlow<NoteNavigationEvent>(NoteNavigationEvent.None)
    val navigationEvent: StateFlow<NoteNavigationEvent> get() = _navigationEvent

    fun loadNote(context: Context, id: Long?) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            if (id == null) {
                _uiState.update { it.copy(isLoading = false) }
                return@launch
            } else {
                val response = noteRetriever.retrieveNote(RetrieveNoteRequestDto(id), context)
                when (response) {
                    is NoteResponse.Success -> {
                        response.note!!.let { note ->
                            _uiState.value = NoteUiState(
                                noteId = note.id,
                                title = note.title,
                                description = note.description,
                                updatedAt = note.updatedAt
                            )
                        }
                    }
                    is NoteResponse.Failure -> {
                        _uiState.update { it.copy(errorMessage = response.exception.errors.firstOrNull()?.detail ?: "Error on loading note") }
                    }
                }
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun onTitleChange(newTitle: String) {
        _uiState.update { it.copy(title = newTitle) }
    }

    fun onDescriptionChange(newDesc: String) {
        _uiState.update { it.copy(description = newDesc) }
    }

    fun onBackClick(context: Context) {
        if (_uiState.value.noteId == null){
            viewModelScope.launch {
                val response = noteCreator.createNote(
                    CreateNoteRequestDto(
                        title = _uiState.value.title,
                        description = _uiState.value.description
                    ),
                    context
                )
                when (response){
                    is NoteResponse.Success -> _navigationEvent.value = NoteNavigationEvent.ToHome
                    is NoteResponse.Failure -> _uiState.value.errorMessage = response.exception.errors.firstOrNull()?.detail ?: "Error on updating note"
                }
            }
        }
        else {
            viewModelScope.launch {
                val response = noteUpdater.updateNote(
                    UpdateNoteRequestDto(
                        id = _uiState.value.noteId!!,
                        title = _uiState.value.title,
                        description = _uiState.value.description
                    ),
                    context
                )
                when (response){
                    is NoteResponse.Success -> _navigationEvent.value = NoteNavigationEvent.ToHome
                    is NoteResponse.Failure -> _uiState.value.errorMessage = response.exception.errors.firstOrNull()?.detail ?: "Error on updating note"
                }
            }
        }
    }

    fun onDeleteClick() {
        _uiState.update { it.copy(showDeleteDialog = true) }
    }

    fun onDeleteCancel() {
        _uiState.update { it.copy(showDeleteDialog = false) }
    }

    fun onDeleteConfirm(context: Context) {
        if (_uiState.value.noteId == null){
            _uiState.update { it.copy(showDeleteDialog = false) }
            _navigationEvent.value = NoteNavigationEvent.ToHome
        }
        else{
            viewModelScope.launch {
                val response = noteDestroyer.destroyNote(DestroyNoteRequestDto(_uiState.value.noteId!!), context)
                when (response){
                    is NoteResponse.Success -> {
                        _uiState.update { it.copy(showDeleteDialog = false) }
                        _navigationEvent.value = NoteNavigationEvent.ToHome
                    }
                    is NoteResponse.Failure -> _uiState.value.errorMessage = response.exception.errors.firstOrNull()?.detail ?: "Error on deleting note"
                }
            }
        }
    }

    fun onNavigationHandled() {
        _navigationEvent.value = NoteNavigationEvent.None
    }
}