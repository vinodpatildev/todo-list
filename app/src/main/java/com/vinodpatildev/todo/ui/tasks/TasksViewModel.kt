package com.vinodpatildev.todo.ui.tasks

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.vinodpatildev.todo.data.TaskDao
import javax.inject.Inject

class TasksViewModel @ViewModelInject constructor(private val taskDao: TaskDao):ViewModel() {

}