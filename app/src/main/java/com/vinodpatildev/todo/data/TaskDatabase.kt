package com.vinodpatildev.todo.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.vinodpatildev.todo.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [Task::class], version = 1)
abstract class TaskDatabase : RoomDatabase() {
    abstract fun taskDao() : TaskDao

    class Callback @Inject constructor(
        private val database: Provider<TaskDatabase>,
        @ApplicationScope val applicationScope: CoroutineScope
    ) : RoomDatabase.Callback(){
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            val dao = database.get().taskDao()

            applicationScope.launch {
                dao.insert(Task("Wash your hands."))
                dao.insert(Task("Do the laundry.", completed = true))
                dao.insert(Task("Buy groceries."))
                dao.insert(Task("Prepare food."))
                dao.insert(Task("Call mom.", completed = true))
                dao.insert(Task("Dring water.", completed = true))
                dao.insert(Task("Solve Leetcode question.", important = true))
            }

        }
    }
}