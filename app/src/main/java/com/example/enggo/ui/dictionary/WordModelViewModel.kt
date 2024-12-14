package com.example.enggo.ui.dictionary

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.enggo.data.dictionary.DictionaryBaseRepository
import com.example.enggo.data.service.UserService
import com.example.enggo.model.dictionary.WordModel
import com.example.enggo.model.dictionary.WordState
import com.example.enggo.ui.course.CourseViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class WordModelViewModel(
    private val dictRepository: DictionaryBaseRepository,
    private val userService: UserService,
    private val currentUserId: String
) : ViewModel() {
    var searchQuery = MutableStateFlow(String())
        private set

    var wordState = MutableStateFlow(WordState())
        private set

    var suggestions = MutableStateFlow(emptyList<String>())
        private set

    private var bookmarkStatus = MutableStateFlow<Boolean?>(null)

    private var searchJob: Job? = null
    private var prefixMatchJob: Job? = null

    fun searcher(query: String, isWordClick: Boolean = false) {
        if (query.isBlank()) {
            clearSuggestions()
            return
        }
        searchJob?.cancel()

        searchJob = viewModelScope.launch(IO) {
            val result = dictRepository.search(query).firstOrNull()?.first()
            result?.let {
                wordState.value = WordState(it.toWordModel())
            }
        }
    }

    fun prefixMatcher(query: String) {
        clearSuggestions()

        prefixMatchJob?.cancel()
        prefixMatchJob = viewModelScope.launch(IO) {
            dictRepository.prefixMatch(query).collect { matches ->
                matches.let { match ->
                    suggestions.value = match.map { it.word }
                    Log.d("PrefixMatcher", "Updated suggestions: ${suggestions.value}")
                }
            }
        }
    }

    fun clearSuggestions() {
        suggestions.value = emptyList()
    }

    fun addBookmark(word: WordModel) {
        viewModelScope.launch(IO) {
            val bookmarks = userService.getBookmarks(currentUserId)
            val alreadyBookmarked = bookmarks.any { it.wordsetId == word.wordsetId }

            if (alreadyBookmarked) {
                Log.d("Bookmark", "Word already bookmarked: ${word.word}")
                bookmarkStatus.value = false
            } else {
                val success = userService.addBookmark(currentUserId, word)
                bookmarkStatus.value = success
                if (success) {
                    Log.d("Bookmark", "Word bookmarked successfully: ${word.word}")
                } else {
                    Log.e("Bookmark", "Failed to bookmark word: ${word.word}")
                }
            }
        }
    }

    fun clearWordModel() {
        wordState.value = WordState()
    }

}