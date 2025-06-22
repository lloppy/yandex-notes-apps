package com.korniiienko.notesapp.navigation


import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.korniiienko.model.AppTheme
import com.korniiienko.notesapp.ui.screens.add.AddNoteScreen
import com.korniiienko.notesapp.ui.screens.edit.EditNoteScreen
import com.korniiienko.notesapp.ui.screens.notes.NotesScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(
    navController: NavHostController,
    appTheme: AppTheme,
    modifier: Modifier = Modifier
) {
    NavHost(navController = navController, startDestination = Screen.MainNotes.route) {
        composable(route = Screen.MainNotes.route) {
            NotesScreen(
                onClickAddNote = {
                    navController.navigate(Screen.AddNote.route)
                },
                onClickOpenNote = { noteId ->
                    navController.navigate(Screen.EditNote.createRoute(noteId))
                },
                appTheme = appTheme,
                modifier = Modifier
            )
        }

        composable(
            route = Screen.EditNote.routePattern,
            arguments = listOf(
                navArgument(Screen.EditNote.argName) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val noteUid = backStackEntry.arguments?.getString(Screen.EditNote.argName)
                ?: throw IllegalArgumentException("noteId parameter wasn't found")
            EditNoteScreen(
                noteUid = noteUid,
                navigateBack = { navController.navigateUp() },
                modifier = Modifier
            )
        }

        composable(route = Screen.AddNote.route) {
            AddNoteScreen(
                navigateBack = { navController.navigateUp() },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
