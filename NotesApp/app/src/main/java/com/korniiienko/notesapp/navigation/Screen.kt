package com.korniiienko.notesapp.navigation

sealed class Screen(val route: String, val name: String) {

    object MainNotes : Screen("main_notes", "Мои заметки")

    object AddNote : Screen("create_note", "Новая заметка")

    data class EditNote(val noteId: String) : Screen("edit_note/{noteId}", "Редактирование") {
        companion object {
            const val routePattern = "edit_note/{noteId}"
            const val argName = "noteId"
            val name = "Редактирование"
            fun createRoute(noteId: String) = "edit_note/$noteId"
        }
    }
}
