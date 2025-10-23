package org.elnix.notes

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.elnix.notes.data.SettingsStore
import org.elnix.notes.ui.NoteViewModel
import org.elnix.notes.ui.theme.adjustBrightness

sealed class Screen(val route: String, val label: String, val icon: @Composable () -> Unit) {
    object Notes : Screen("notes", "Notes", { Icon(Icons.Default.Add, contentDescription = "notes") })
    object Settings : Screen("settings", "Settings", { Icon(Icons.Default.Settings, contentDescription = "settings") })
    object Edit : Screen("edit/{noteId}", "Edit", { Icon(Icons.Default.Add, contentDescription = "edit") }) {
        fun createRoute(noteId: Long) = "edit/$noteId"
    }
    object Create : Screen("create", "Create", { Icon(Icons.Default.Add, contentDescription = "create") })
}

@Composable
fun MainApp(vm: NoteViewModel) {
    val navController = rememberNavController()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    Scaffold(
        bottomBar = { BottomNav(navController) },
        floatingActionButton = {
            if (currentRoute != Screen.Create.route && currentRoute != Screen.Edit.route) {
                FloatingActionButton(
                    onClick = {
                        navController.navigate(Screen.Create.route) {
                            launchSingleTop = true
                            popUpTo(Screen.Create.route) { inclusive = true }
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.background,
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add",
                    )
                }
            }

        }
    ) { innerPadding ->
        NavHost(navController, startDestination = Screen.Notes.route, Modifier.padding(innerPadding)) {
            // NOTES LIST
            composable(Screen.Notes.route) {
                NotesScreen(vm, navController)
            }


            // Settings list
            composable(Screen.Settings.route) { SettingsListScreen(navController) }

            // Settings sub-screens
            composable("settings/appearance") { AppearanceSettingsScreen(navController) }
                // sub-sub-setting screen for color customisation
                composable("settings/appearance/colors") { ColorSelectorSettingsScreen(navController)}
            composable("settings/customisation") { CustomisationSettingsScreen(navController) }
            composable("settings/reminders") { RemindersSettingsScreen(navController) }
            composable("settings/backup") { BackupSettingsScreen(navController) }
            composable("settings/debug") { DebugSettingsScreen(navController, vm) }


            // CREATE NOTE (no note created here — NoteEditorScreen handles it)
            composable(Screen.Create.route) {
                NoteEditorScreen(
                    vm = vm,
                    noteId = null, // indicates new note
                    onSaved = { navController.popBackStack() },
                    onCancel = { navController.popBackStack() }
                )
            }

            // EDIT NOTE
            composable(
                route = Screen.Edit.route,
                arguments = listOf(navArgument("noteId") { type = NavType.LongType })
            ) { backStackEntry ->
                val noteId = backStackEntry.arguments?.getLong("noteId") ?: return@composable
                NoteEditorScreen(
                    vm = vm,
                    noteId = noteId,
                    onSaved = { navController.popBackStack() },
                    onCancel = { navController.popBackStack() }
                )
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
//        containerColor = MaterialTheme.colorScheme.background.blendWith(MaterialTheme.colorScheme.primary, 0.2f)
        containerColor = MaterialTheme.colorScheme.surface

    ) {
        val current = navController.currentBackStackEntryAsState().value?.destination?.route

        items.forEach { screen ->
            NavigationBarItem(
                selected = current == screen.route,
                onClick = { navController.navigate(screen.route) { launchSingleTop = true } },
                icon = screen.icon,
                label = if (showLabels == false) null else {
                    { Text(screen.label, color = MaterialTheme.colorScheme.onBackground) }
                },
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
