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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.elnix.notes.data.settings.ShowNavBarActions
import org.elnix.notes.data.settings.UiSettingsStore
import org.elnix.notes.ui.NoteViewModel
import org.elnix.notes.ui.security.LockScreen
import org.elnix.notes.ui.theme.adjustBrightness

sealed class Screen(val route: String, val label: String, val icon: @Composable () -> Unit) {
    object Notes : Screen("notes", "Notes", { Icon(Icons.Default.Add, contentDescription = "notes") })
    object Settings : Screen("settings", "Settings", { Icon(Icons.Default.Settings, contentDescription = "settings") }) {
        fun appearanceTab() = "${route}/appearance"
        fun customisationTab() = "${route}/customisation"
        fun reminderTab() = "${route}/reminder"
        fun securityTab() = "${route}/security"
        fun backupTab() = "${route}/backup"
        fun debugTab() = "${route}/debug"
    }
    object Edit : Screen("edit/{noteId}", "Edit", { Icon(Icons.Default.Add, contentDescription = "edit") })
    object Create : Screen("create", "Create", { Icon(Icons.Default.Add, contentDescription = "create") })
}

@Composable
fun MainApp(vm: NoteViewModel, activity: FragmentActivity) {
    val navController = rememberNavController()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    var unlocked by remember { mutableStateOf(false) }


    if (!unlocked) {
        LockScreen(activity) {
            unlocked = true
        }
    } else {
        Scaffold(
            bottomBar = { BottomNav(navController) },
            floatingActionButton = {
                if (currentRoute == Screen.Notes.route) {
                    FloatingActionButton(
                        onClick = {
                            navController.navigate(Screen.Create.route) {
                                launchSingleTop = true
                                popUpTo(Screen.Create.route)
                            }
                        },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
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


                // SETTINGS LIST
                composable(Screen.Settings.route) { SettingsListScreen(navController) }

                composable(Screen.Settings.appearanceTab()) { AppearanceSettingsScreen(navController) }
                    // sub-sub-setting screen for color customisation
                    composable("settings/appearance/colors") { ColorSelectorSettingsScreen(navController)}
                composable(Screen.Settings.customisationTab()) { CustomisationSettingsScreen(navController) }
                composable(Screen.Settings.reminderTab()) { RemindersSettingsScreen(navController) }
                composable(Screen.Settings.securityTab()) { SecuritySettingsScreen(navController) }
                composable(Screen.Settings.backupTab()) { BackupSettingsScreen(navController) }
                composable(Screen.Settings.debugTab()) { DebugSettingsScreen(navController, vm) }


                // CREATE NOTE
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
}

@Composable
fun BottomNav(navController: NavHostController) {
    val ctx = LocalContext.current
    val showNavbarLabel by UiSettingsStore.getShowBottomNavLabelsFlow(ctx)
        .collectAsState(initial = ShowNavBarActions.ALWAYS)

    val items = listOf(
        Screen.Notes,
        Screen.Settings
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface

    ) {
        val current = navController.currentBackStackEntryAsState().value?.destination?.route.orEmpty()

        items.forEach { screen ->
            val isCurrentOrSub = current.startsWith(screen.route)
            val showLabel = when (showNavbarLabel) {
                ShowNavBarActions.ALWAYS -> true
                ShowNavBarActions.NEVER -> false
                ShowNavBarActions.SELECTED -> isCurrentOrSub
                ShowNavBarActions.OTHER -> !isCurrentOrSub
            }

            NavigationBarItem(
                selected = current == screen.route,
                onClick = { navController.navigate(screen.route) { launchSingleTop = true } },
                icon = screen.icon,
                label = if (!showLabel) null else {
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
