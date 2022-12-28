package com.vinodpatildev.todo.ui.tasks

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.vinodpatildev.todo.data.PreferencesManager
import com.vinodpatildev.todo.data.SortOrder
import com.vinodpatildev.todo.data.TaskDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class TasksViewModel @ViewModelInject constructor(
    private val taskDao: TaskDao,
    private val preferencesManager: PreferencesManager
    ) : ViewModel() {
    val searchQuery = MutableStateFlow<String>("")

    val preferencesFlow = preferencesManager.preferencesFlow

    private val tasksFlow = combine(
        searchQuery,
        preferencesFlow
    ) { query, filterPreferences ->
        Pair(query, filterPreferences)
    }.flatMapLatest { (query, filterPreferences) ->
        taskDao.getTasks(query, filterPreferences.sortOrder, filterPreferences.hideCompleted)
    }
    fun onSortOrderSelected(sortOrder: SortOrder) = viewModelScope.launch {
        preferencesManager.updateSortOrder(sortOrder)
    }
    fun onHideCompletedClick(hideCompleted:Boolean) = viewModelScope.launch {
        preferencesManager.updateHideCompleted(hideCompleted)
    }
    val tasks = tasksFlow.asLiveData()
}