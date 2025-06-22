package com.yuaatn.instagram_notes.navigation

sealed class Screen(val route: String) {
    object Home : Screen(route = "home_screen")
    object Setting : Screen(route = "setting_screen")

    object AddNote : Screen(route = "add_note")
    object EditNote : Screen(route = "edit_note")

}