package com.vinodpatildev.todo.ui.addedittask

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vinodpatildev.todo.data.Task
import com.vinodpatildev.todo.data.TaskDao
import com.vinodpatildev.todo.ui.ADD_TASK_RESULT_OK
import com.vinodpatildev.todo.ui.EDIT_TASK_RESULT_OK
import com.vinodpatildev.todo.ui.MainActivity
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class AddEditTaskViewModel @ViewModelInject constructor(
    private val taskDao: TaskDao,
    @Assisted private val state: SavedStateHandle
) : ViewModel() {

    val task = state.get<Task>("task")

    var taskName = state.get<String>("taskName") ?: task?.name ?: ""
        set(value) {
            field = value
            state.set("taskName",value)
        }

    var taskImportant = state.get<Boolean>("taskImportant") ?: task?.important ?: false
        set(value) {
            field = value
            state.set("taskImportant",value)
        }

    private val addEditTaskEventChannel = Channel<AddEditTaskEvent>()
    val addEdiTaskEvent = addEditTaskEventChannel.receiveAsFlow()

    fun onFabSaveTaskClick() {
        if (taskName.isBlank()) {
            //Show invalid input message
            showInvalidInputMessage("Name cannot be empty.")
            return
        }
        if(task != null){
            val updatedTask = task.copy(name = taskName, important = taskImportant)
            updatedTask(updatedTask)
        } else {
            val newTask = Task(name = taskName, important = taskImportant)
            createTask(newTask)
        }
    }

    private fun showInvalidInputMessage(msg: String) = viewModelScope.launch {
        addEditTaskEventChannel.send(AddEditTaskEvent.ShowInvalidInputMessage(msg))

    }
    private fun createTask(newTask: Task) = viewModelScope.launch {
        taskDao.insert(newTask)
        // navigate back
        addEditTaskEventChannel.send(AddEditTaskEvent.NavigateBackWithResult(ADD_TASK_RESULT_OK))
    }

    private fun updatedTask(updatedTask: Task)  = viewModelScope.launch {
        taskDao.update(updatedTask)
        // navigate back
        addEditTaskEventChannel.send(AddEditTaskEvent.NavigateBackWithResult(EDIT_TASK_RESULT_OK))
    }

    sealed class AddEditTaskEvent{
        data class ShowInvalidInputMessage(val msg:String) : AddEditTaskEvent()
        data class NavigateBackWithResult(val result:Int) : AddEditTaskEvent()
    }

}