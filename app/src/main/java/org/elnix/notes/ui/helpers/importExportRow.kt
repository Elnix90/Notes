package org.elnix.notes.ui.helpers

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import org.elnix.notes.data.SettingsStore
import org.elnix.notes.ui.theme.AppObjectsColors
import org.json.JSONObject
import java.io.File

@Composable
fun ExportImportRow(
    ctx: Context,
    primaryColor: Int,
    backgroundColor: Int,
    onBackgroundColor: Int,
    showNavBarLabels: Boolean
) {
    val scope = rememberCoroutineScope()

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            ctx.contentResolver.openInputStream(uri)?.use {
                val json = it.bufferedReader().readText()
                val obj = JSONObject(json)
                scope.launch {
                    SettingsStore.setPrimary(ctx, obj.optInt("primary"))
                    SettingsStore.setBackground(ctx, obj.optInt("background"))
                    SettingsStore.setOnBackground(ctx, obj.optInt("onBackground"))
                    SettingsStore.setShowBottomNavLabelsFlow(ctx, obj.optBoolean("showNavLabels"))
                }
            }
        }
    }

    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(
            onClick = {
                val file = File(ctx.cacheDir, "settings_export.json")
                val json = JSONObject().apply {
                    put("primary", primaryColor)
                    put("background", backgroundColor)
                    put("onBackground", onBackgroundColor)
                    put("showNavLabels", showNavBarLabels)
                }.toString()
                file.writeText(json)
            },
            colors = AppObjectsColors.buttonColors()
        ) {
            Text("Export Settings")
        }

        Button(
            onClick = { launcher.launch(arrayOf("application/json")) },
            colors = AppObjectsColors.buttonColors()
        ) {
            Text("Import Settings")
        }
    }
}
