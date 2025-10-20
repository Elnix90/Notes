package org.elnix.notes

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.elnix.notes.data.SettingsStore
import org.json.JSONObject
import java.io.File

@Composable
fun SettingsScreen() {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val showNavbarLabels by SettingsStore.getShowBottomNavLabelsFlow(ctx).collectAsState(initial = true)

    val primary by SettingsStore.getPrimaryFlow(ctx).collectAsState(initial = null)
    val background by SettingsStore.getBackgroundFlow(ctx).collectAsState(initial = null)
    val onBackground by SettingsStore.getOnBackgroundFlow(ctx).collectAsState(initial = null)

    CompositionLocalProvider(
        LocalContentColor provides MaterialTheme.colorScheme.onBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text("Custom Colors", style = MaterialTheme.typography.titleMedium)

            ColorPickerRow("Primary", primary ?: MaterialTheme.colorScheme.primary.toArgb()) {
                scope.launch { SettingsStore.setPrimary(ctx, it) }
            }

            ColorPickerRow("Background", background ?: MaterialTheme.colorScheme.background.toArgb()) {
                scope.launch { SettingsStore.setBackground(ctx, it) }
            }

            ColorPickerRow("On Background (Text)", onBackground ?: MaterialTheme.colorScheme.onBackground.toArgb()) {
                scope.launch { SettingsStore.setOnBackground(ctx, it) }
            }

            HorizontalDivider()

            Button(
                onClick = { scope.launch { SettingsStore.resetColors(ctx) } },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onBackground
                )
            ) {
                Text("Reset to Defaults")
            }

            HorizontalDivider()

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Show Navigation Bar Labels", color = MaterialTheme.colorScheme.onBackground)
                Switch(
                    checked = showNavbarLabels ?: true,
                    onCheckedChange = { scope.launch { SettingsStore.setShowBottomNavLabelsFlow(ctx, it) } }
                )
            }

            HorizontalDivider()


            ExportImportRow(
                ctx = ctx,
                primaryColor = primary ?: MaterialTheme.colorScheme.primary.toArgb(),
                backgroundColor = background ?: MaterialTheme.colorScheme.background.toArgb(),
                onBackgroundColor = onBackground ?: MaterialTheme.colorScheme.onBackground.toArgb()
            )
        }
    }
}

@Composable
fun ColorPickerRow(label: String, currentColor: Int, onColorPicked: (Int) -> Unit) {
    var showPicker by remember { mutableStateOf(false) }

    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(Color(currentColor), shape = CircleShape)
                    .border(1.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
            )
            Spacer(Modifier.width(8.dp))
            Button(
                onClick = { showPicker = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onBackground
                )
            ) {
                Text("Pick")
            }
        }
    }

    if (showPicker) {
        AlertDialog(
            onDismissRequest = { showPicker = false },
            title = { Text("Pick a $label color") },
            text = {
                ColorPicker(
                    initialColor = Color(currentColor),
                    onColorSelected = {
                        onColorPicked(it.toArgb())
                        showPicker = false
                    }
                )
            },
            confirmButton = {},
            dismissButton = {}
        )
    }
}

@Composable
fun ColorPicker(initialColor: Color, onColorSelected: (Color) -> Unit) {
    var red by remember { mutableFloatStateOf(initialColor.red) }
    var green by remember { mutableFloatStateOf(initialColor.green) }
    var blue by remember { mutableFloatStateOf(initialColor.blue) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Preview")
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(Color(red, green, blue))
                .border(1.dp, MaterialTheme.colorScheme.outline)
        )

        SliderWithLabel("R", red) { red = it }
        SliderWithLabel("G", green) { green = it }
        SliderWithLabel("B", blue) { blue = it }

        Spacer(Modifier.height(12.dp))
        Button(
            onClick = { onColorSelected(Color(red, green, blue)) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onBackground
            )
        ) {
            Text("Apply")
        }
    }
}

@Composable
private fun SliderWithLabel(label: String, value: Float, onChange: (Float) -> Unit) {
    Column {
        Text("$label: ${(value * 255).toInt()}")
        Slider(value = value, onValueChange = onChange, valueRange = 0f..1f, steps = 254)
    }
}

@Composable
fun ExportImportRow(
    ctx: Context,
    primaryColor: Int,
    backgroundColor: Int,
    onBackgroundColor: Int
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
            val file = File(ctx.cacheDir, "theme_export.json")
            val json = JSONObject().apply {
                put("primary", primaryColor)
                put("background", backgroundColor)
                put("onBackground", onBackgroundColor)
            }.toString()
            file.writeText(json)
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onBackground
            )
        ) {
            Text("Export Theme")
        }

        Button(
            onClick = { launcher.launch(arrayOf("application/json")) },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onBackground
            )
        ) {
            Text("Import Theme")
        }
    }
}

