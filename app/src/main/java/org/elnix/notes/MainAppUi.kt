package org.elnix.notes

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.launch
import org.elnix.notes.data.helpers.NoteType
import org.elnix.notes.data.helpers.OffsetItem
import org.elnix.notes.data.helpers.TagItem
import org.elnix.notes.data.helpers.ToolBars
import org.elnix.notes.data.settings.stores.OffsetsSettingsStore
import org.elnix.notes.data.settings.stores.TagsSettingsStore
import org.elnix.notes.data.settings.stores.UiSettingsStore
import org.elnix.notes.ui.NoteViewModel
import org.elnix.notes.ui.editors.DrawingEditorScreen
import org.elnix.notes.ui.editors.UnifiedTextualNotesEditor
import org.elnix.notes.ui.security.LockScreen
import org.elnix.notes.ui.welcome.WelcomeScreen
import org.elnix.notes.ui.whatsnew.Update
import org.elnix.notes.ui.whatsnew.WhatsNewBottomSheet
import kotlin.random.Random

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

        object RemindersSub {
            const val NOTIFICATIONS = "settings/reminders/notifications"
        }
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
fun MainApp(
    vm: NoteViewModel,
    activity: FragmentActivity,
    startNoteId: Long? = null,
    startNoteType: NoteType = NoteType.TEXT
) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    val navController = rememberNavController()
    val locked by vm.locked.collectAsState()

    val hasSeenWelcome by UiSettingsStore.getHasShownWelcome(ctx).collectAsState(true)
    val hasInitialized by UiSettingsStore.getHasInitialized(ctx).collectAsState(true)
    val lastSeenVersion by UiSettingsStore.getLastSeenVersion(ctx).collectAsState(0)
    val currentVersion = BuildConfig.VERSION_CODE


    val updates = listOf(
        Update(
            "1.0.0",
            listOf("Added what's new screen", "Added welcome screen", "Changed app icon")
        )
    )


    when {
        locked -> LockScreen(activity) { vm.unlock() }

        !hasSeenWelcome -> {
            WelcomeScreen(
                onFinish = {
                    scope.launch{
                        UiSettingsStore.setHasShownWelcome(ctx, true)

                        // Initialization block - Where I put all the vars that need an init state
                        if (!hasInitialized) {
                            for (offsetItem in listOf(
                                OffsetItem(id = Random.nextLong(), offset = 600),
                                OffsetItem(id = Random.nextLong(), offset = 1800),
                                OffsetItem(id = Random.nextLong(), offset = 3600),
                                OffsetItem(id = Random.nextLong(), offset = 86400)
                            )) {
                                OffsetsSettingsStore.addOffset(ctx, offsetItem)
                            }

                            for (item in listOf(
                                TagItem(id = Random.nextLong(), name = "Imp", color = Color.Yellow),
                                TagItem(id = Random.nextLong(), name = "Todo", color = Color.DarkGray),
                                TagItem(id = Random.nextLong(), name = "Home", color = Color.Blue),
                                TagItem(id = Random.nextLong(), name = "Work", color = Color.Red),
                            )) {
                                TagsSettingsStore.addTag(ctx, item)
                            }


                            UiSettingsStore.setHasInitialized(ctx, true)
                        }
                    }
                }
            )
        }

        else -> {

            NavHost(
                navController = navController,
                startDestination = if (startNoteId != null) {
                    "edit/$startNoteId?type=${startNoteType.name}"
                } else Routes.NOTES
            ) {
                // NOTES
                composable(Routes.NOTES) {
                    var showWhatsNew by remember { mutableStateOf(false) }

                    LaunchedEffect(lastSeenVersion, currentVersion) {
                        if (lastSeenVersion < currentVersion) {
                            showWhatsNew = true
                        }
                    }

                    Box(modifier = Modifier.fillMaxSize()) {
                        // Main notes screen
                        NotesScreen(vm, navController)

                        // Overlay the "What's New" popup
                        if (showWhatsNew) {
                            WhatsNewBottomSheet(
                                updates = updates,
                                onDismiss = {
                                    scope.launch {
                                        UiSettingsStore.setLastSeenVersion(ctx, currentVersion)
                                        showWhatsNew = false
                                    }
                                }
                            )
                        }
                    }

                }

                // SETTINGS NAV GRAPH
                settingsNavGraph(navController, vm, activity)

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
                            activity = activity,
                            navController = navController,
                            noteId = null,
                            noteType = noteType
                        ) { navController.navigate(Routes.NOTES) }

                        NoteType.DRAWING -> DrawingEditorScreen(
                            vm,
                            null,
                            onSaved = { navController.navigate(Routes.NOTES) },
                            onCancel = { navController.navigate(Routes.NOTES) }
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
                            activity = activity,
                            navController = navController,
                            noteId = noteId,
                            noteType = noteType
                        ) { navController.navigate(Routes.NOTES) }

                        NoteType.DRAWING -> DrawingEditorScreen(
                            vm,
                            noteId,
                            onSaved = { navController.navigate(Routes.NOTES) },
                            onCancel = { navController.navigate(Routes.NOTES) }
                        )
                    }
                }
            }
        }
    }
}

// -------------------- SETTINGS NAV GRAPH --------------------
fun NavGraphBuilder.settingsNavGraph(navController: NavHostController, vm: NoteViewModel, activity: FragmentActivity) {
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

        composable(Routes.Settings.REMINDER) { RemindersSettingsScreen(navController, activity) }
        composable(Routes.Settings.RemindersSub.NOTIFICATIONS) { NotificationsSettingsScreen(navController) }

        composable(Routes.Settings.SECURITY) { SecuritySettingsScreen(navController) }
        composable(Routes.Settings.BACKUP) { BackupSettingsScreen(navController, activity) }
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