package org.elnix.notes

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.elnix.notes.data.helpers.NoteType
import org.elnix.notes.data.helpers.ToolBars
import org.elnix.notes.ui.NoteViewModel
import org.elnix.notes.ui.editors.DrawingEditorScreen
import org.elnix.notes.ui.editors.UnifiedTextualNotesEditor
import org.elnix.notes.ui.security.LockScreen

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

        object CustomisationSub {
            const val TOOLBARS = "settings/customisation/toolbars"
            const val TOOLBAR_EDITOR = "settings/customisation/toolbar"
        }
        const val REMINDER = "settings/reminder"
        const val SECURITY = "settings/security"
        const val BACKUP = "settings/backup"
        const val DEBUG = "settings/debug"
        const val LANGUAGE = "settings/language"
        const val PLUGINS = "settings/plugins"

        object DebugSub {
            const val REMINDERS = "settings/debug/reminders"
            const val NOTES = "settings/debug/notes"
            const val OTHER = "settings/debug/other"
            const val USER_CONFIRM = "settings/debug/user_confirm"
        }
    }
}

// -------------------- MAIN APP --------------------
@Composable
fun MainApp(vm: NoteViewModel, activity: FragmentActivity) {
    val navController = rememberNavController()
    var unlocked by remember { mutableStateOf(false) }

    if (!unlocked) {
        LockScreen(activity) { unlocked = true }
    } else {

        NavHost(
            navController = navController,
            startDestination = Routes.NOTES
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
                    NoteType.TEXT, NoteType.CHECKLIST -> UnifiedTextualNotesEditor(
                        vm = vm,
                        navController = navController,
                        noteId = null,
                        noteType = noteType
                    ) { navController.navigate(Routes.NOTES) }

                    NoteType.DRAWING -> DrawingEditorScreen(
                        vm,
                        null,
                        onSaved = { navController.navigate(Routes.NOTES)  },
                        onCancel = { navController.navigate(Routes.NOTES)  }
                    )
                }
            }

            // EDIT NOTE
            composable(
                route = "${Routes.EDIT}?type={type}",
                arguments = listOf(
                    navArgument("noteId") { type = NavType.LongType },
                    navArgument("type") {
                        type = NavType.StringType
                        defaultValue = NoteType.TEXT.name
                    }
                )
            ) { backStackEntry ->
                val noteId = backStackEntry.arguments?.getLong("noteId") ?: return@composable
                val typeArg = backStackEntry.arguments?.getString("type") ?: NoteType.TEXT.name
                val noteType = NoteType.valueOf(typeArg)

                when (noteType) {
                    NoteType.TEXT, NoteType.CHECKLIST -> UnifiedTextualNotesEditor(
                        vm = vm,
                        navController = navController,
                        noteId = noteId,
                        noteType = noteType
                    ) { navController.navigate(Routes.NOTES) }
                    NoteType.DRAWING -> DrawingEditorScreen(
                        vm,
                        noteId,
                        onSaved = { navController.navigate(Routes.NOTES)  },
                        onCancel = { navController.navigate(Routes.NOTES)  }
                    )
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
        composable(Routes.Settings.ROOT) { SettingsListScreen(navController) { navController.navigate(Routes.NOTES) } }
        composable(Routes.Settings.APPEARANCE) { AppearanceSettingsScreen(navController) }
        composable(Routes.Settings.COLORS) { ColorSelectorSettingsScreen(navController) }

        composable(Routes.Settings.CUSTOMISATION) { CustomisationSettingsScreen(navController) }
        composable(Routes.Settings.CustomisationSub.TOOLBARS) { ToolbarsOrderSettingsScreen(navController) }

        composable(
            route = "${Routes.Settings.CustomisationSub.TOOLBAR_EDITOR}?toolbar={toolbar}",
            arguments = listOf(
                navArgument("toolbar") { type = NavType.StringType },
            )
        ) { backStackEntry ->
            val toolbarKey = backStackEntry.arguments?.getString("toolbar") ?: return@composable
            val toolbarSetting = ToolBars.valueOf(toolbarKey)

            ToolbarsCustomisationSettingsScreen(
                navController = navController,
                toolbar = toolbarSetting
            )
        }

        composable(Routes.Settings.REMINDER) { RemindersSettingsScreen(navController) }
        composable(Routes.Settings.SECURITY) { SecuritySettingsScreen(navController) }
        composable(Routes.Settings.BACKUP) { BackupSettingsScreen(navController) }
        composable(Routes.Settings.LANGUAGE) { LanguageSettingsScreen(navController) }
        composable(Routes.Settings.PLUGINS) { PluginsSettingsScreen(navController) }

        composable(Routes.Settings.DEBUG) { DebugSettingsScreen(navController) }
        // Debug sub-settings
        composable(Routes.Settings.DebugSub.REMINDERS) { DebugReminderSettingsScreen(navController, vm) }
        composable(Routes.Settings.DebugSub.NOTES) { DebugNotesSettingsScreen(navController, vm) }
        composable(Routes.Settings.DebugSub.OTHER) { OtherSettingsScreen(navController) }
        composable(Routes.Settings.DebugSub.USER_CONFIRM) { UserConfirmSettingsScreen(navController) }
    }
}