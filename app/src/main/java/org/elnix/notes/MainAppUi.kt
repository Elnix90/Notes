// file: org/elnix/notes/MainAppUi.kt
package org.elnix.notes

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import org.elnix.notes.data.SettingsStore
import org.elnix.notes.ui.NoteViewModel
import org.elnix.notes.ui.theme.adjustBrightness
import org.elnix.notes.ui.theme.blendWith

sealed class Screen(val route: String, val label: String, val icon: @Composable () -> Unit) {
    object Notes : Screen("notes", "Notes", { Icon(Icons.Default.Add, contentDescription = "notes") })
    object Settings : Screen("settings", "Settings", { Icon(Icons.Default.Settings, contentDescription = "settings") })
    object Edit : Screen("edit/{noteId}", "Edit", { Icon(Icons.Default.Edit, contentDescription = "edit") }) {
        fun createRoute(noteId: Long) = "edit/$noteId"
    }
    object Create : Screen("create", "Create", { Icon(Icons.Default.Add, contentDescription = "create") })
}

@Composable
fun MainApp(vm: NoteViewModel) {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = { BottomNav(navController) },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(Screen.Create.route) }) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { innerPadding ->
        NavHost(navController, startDestination = "notes", Modifier.padding(innerPadding)) {
            composable("notes") { NotesScreen(vm, navController) }
            composable("settings") { SettingsScreen() }
            composable(Screen.Create.route) {
                NoteEditorScreen(onSave = { title, desc, dueDateTime, reminderEnabled ->
                    vm.addNote(title, desc, dueDateTime, reminderEnabled)
                    navController.popBackStack()
                })

            }
            composable(
                route = "edit/{noteId}",
                arguments = listOf(navArgument("noteId") { type = NavType.LongType })
            ) { backStackEntry ->
                val noteId = backStackEntry.arguments?.getLong("noteId") ?: 0L
                EditNoteScreen(noteId = noteId, vm = vm, onSaved = { navController.popBackStack() })
            }
        }
    }
}

@Composable
fun BottomNav(navController: NavHostController) {
    val ctx = LocalContext.current
    val showLabels by SettingsStore.getShowBottomNavLabelsFlow(ctx).collectAsState(initial = true)

    val items = listOf(
        Screen.Notes,
        Screen.Settings
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.background.blendWith(MaterialTheme.colorScheme.primary, 0.2f)
    ) {
        val current = navController.currentBackStackEntryAsState().value?.destination?.route
        items.forEach { screen ->
            NavigationBarItem(
                selected = current == screen.route,
                onClick = { navController.navigate(screen.route) { launchSingleTop = true } },
                icon = screen.icon,
                label = if (showLabels == false) null else { { Text(screen.label, color = MaterialTheme.colorScheme.onBackground) } },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary.adjustBrightness(1.5f),
                    unselectedIconColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedTextColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}
