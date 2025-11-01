package org.elnix.notes

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.elnix.notes.data.helpers.NoteType
import org.elnix.notes.data.settings.ShowNavBarActions
import org.elnix.notes.data.settings.stores.UiSettingsStore
import org.elnix.notes.ui.NoteViewModel
import org.elnix.notes.ui.editors.ChecklistEditorScreen
import org.elnix.notes.ui.editors.DrawingEditorScreen
import org.elnix.notes.ui.editors.NoteEditorScreen
import org.elnix.notes.ui.helpers.AddNoteFab
import org.elnix.notes.ui.security.LockScreen
import org.elnix.notes.ui.theme.adjustBrightness

// -------------------- ROUTES --------------------
object Routes {
    const val NOTES = "notes"
    const val CREATE = "create"
    const val EDIT = "edit/{noteId}"

    object Settings {
        const val ROOT = "settings"
        const val APPEARANCE = "settings/appearance"
        const val COLORS = "settings/appearance/colors"
        const val CUSTOMISATION = "settings/customisation"
        const val REMINDER = "settings/reminder"
        const val SECURITY = "settings/security"
        const val BACKUP = "settings/backup"
        const val DEBUG = "settings/debug"
        const val LANGUAGE = "settings/language"

        object DebugSub {
            const val REMINDERS = "settings/debug/reminders"
            const val NOTES = "settings/debug/notes"
            const val OTHER = "settings/debug/other"
        }
    }
}

// -------------------- MAIN APP --------------------
@Composable
fun MainApp(vm: NoteViewModel, activity: FragmentActivity) {
    val navController = rememberNavController()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route.orEmpty()
    var unlocked by remember { mutableStateOf(false) }

    if (!unlocked) {
        LockScreen(activity) { unlocked = true }
    } else {
        Scaffold(
            bottomBar = { BottomNav(navController) },
            floatingActionButton = {
                if (currentRoute == Routes.NOTES) {
                    AddNoteFab(navController)
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Routes.NOTES,
                modifier = Modifier.padding(innerPadding)
            ) {
                // NOTES
                composable(Routes.NOTES) { NotesScreen(vm, navController) }

                // SETTINGS NAV GRAPH
                settingsNavGraph(navController, vm)

                // CREATE NOTE
                composable(
                    route = "${Routes.CREATE}?type={type}",
                    arguments = listOf(navArgument("type") {
                        type = NavType.StringType
                        defaultValue = NoteType.TEXT.name
                    })
                ) { backStackEntry ->
                    val typeArg = backStackEntry.arguments?.getString("type")
                    val noteType = NoteType.valueOf(typeArg ?: NoteType.TEXT.name)

                    when (noteType) {
                        NoteType.TEXT -> NoteEditorScreen(
                            vm,
                            null,
                            onSaved = { navController.popBackStack() },
                            onCancel = { navController.popBackStack() })
                        NoteType.CHECKLIST -> ChecklistEditorScreen(
                            vm,
                            null,
                            onSaved = { navController.popBackStack() },
                            onCancel = { navController.popBackStack() }
                        )
                        NoteType.DRAWING -> DrawingEditorScreen(
                            vm,
                            null,
                            onSaved = { navController.popBackStack() },
                            onCancel = { navController.popBackStack() }
                        )
                    }
                }

                // EDIT NOTE
                composable(
                    route = Routes.EDIT,
                    arguments = listOf(navArgument("type") {
                        type = NavType.StringType
                        defaultValue = NoteType.TEXT.name
                    })
                ) { backStackEntry ->
                    val noteId = backStackEntry.arguments?.getLong("noteId") ?: return@composable
                    val typeArg = backStackEntry.arguments?.getString("type")
                    val noteType = NoteType.valueOf(typeArg ?: NoteType.TEXT.name)

                    when (noteType) {
                        NoteType.TEXT -> NoteEditorScreen(
                            vm,
                            noteId,
                            onSaved = { navController.popBackStack() },
                            onCancel = { navController.popBackStack() })
                        NoteType.CHECKLIST -> ChecklistEditorScreen(
                            vm,
                            noteId,
                            onSaved = { navController.popBackStack() },
                            onCancel = { navController.popBackStack() }
                        )
                        NoteType.DRAWING -> DrawingEditorScreen(
                            vm,
                            noteId,
                            onSaved = { navController.popBackStack() },
                            onCancel = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}

// -------------------- SETTINGS NAV GRAPH --------------------
fun NavGraphBuilder.settingsNavGraph(navController: NavHostController, vm: NoteViewModel) {
    navigation(
        startDestination = Routes.Settings.ROOT,
        route = "settings_graph"
    ) {
        composable(Routes.Settings.ROOT) { SettingsListScreen(navController) }
        composable(Routes.Settings.APPEARANCE) { AppearanceSettingsScreen(navController) }
        composable(Routes.Settings.COLORS) { ColorSelectorSettingsScreen(navController) }
        composable(Routes.Settings.CUSTOMISATION) { CustomisationSettingsScreen(navController) }
        composable(Routes.Settings.REMINDER) { RemindersSettingsScreen(navController) }
        composable(Routes.Settings.SECURITY) { SecuritySettingsScreen(navController) }
        composable(Routes.Settings.BACKUP) { BackupSettingsScreen(navController) }
        composable(Routes.Settings.LANGUAGE) { LanguageSettingsScreen(navController) }

        composable(Routes.Settings.DEBUG) { DebugSettingsScreen(navController) }
        // Debug sub-settings
        composable(Routes.Settings.DebugSub.REMINDERS) { DebugReminderSettingsScreen(navController, vm) }
        composable(Routes.Settings.DebugSub.NOTES) { DebugNotesSettingsScreen(navController, vm) }
        composable(Routes.Settings.DebugSub.OTHER) { OtherSettingsScreen(navController) }
    }
}

// -------------------- BOTTOM NAV --------------------
@Composable
fun BottomNav(navController: NavHostController) {
    val ctx = LocalContext.current
    val showNavbarLabel by UiSettingsStore.getShowBottomNavLabelsFlow(ctx)
        .collectAsState(initial = ShowNavBarActions.ALWAYS)

    val items = listOf(
        BottomNavItem(Routes.NOTES, stringResource(R.string.notes), Icons.AutoMirrored.Filled.FormatListBulleted),
        BottomNavItem(Routes.Settings.ROOT, stringResource(R.string.settings), Icons.Default.Settings)
    )

    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
        val current = navController.currentBackStackEntryAsState().value?.destination?.route.orEmpty()

        items.forEach { item ->
            val isCurrentOrSub = current.startsWith(item.route)
            val showLabel = when (showNavbarLabel) {
                ShowNavBarActions.ALWAYS -> true
                ShowNavBarActions.NEVER -> false
                ShowNavBarActions.SELECTED -> isCurrentOrSub
                ShowNavBarActions.OTHER -> !isCurrentOrSub
            }

            NavigationBarItem(
                selected = current.startsWith(item.route),
                onClick = {
                    if( current != item.route ) {
                        navController.navigate(item.route) { launchSingleTop = true }
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = if (!showLabel) null else { { Text(item.label, color = MaterialTheme.colorScheme.onBackground) } },
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

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)
