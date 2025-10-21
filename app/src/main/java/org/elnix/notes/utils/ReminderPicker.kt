package org.elnix.notes.utils

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import org.elnix.notes.ui.helpers.StyledReminderDialogs
import java.util.Calendar


@Composable
fun ReminderPicker(
    onPicked: (ReminderOffset) -> Unit
) {
    val ctx = LocalContext.current
    var hasPermission by remember { mutableStateOf(false) }

    // Launcher for notification permission
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            hasPermission = ctx.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) ==
                    android.content.pm.PackageManager.PERMISSION_GRANTED
            if (!hasPermission) {
                launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            hasPermission = true // pre-Android 13, permission is automatic
        }
    }

    var tempCalendar by remember { mutableStateOf(Calendar.getInstance()) }

    if (hasPermission) {
        StyledReminderDialogs(tempCalendar = tempCalendar, onPicked = onPicked)
    } else {
        Button(
            onClick = { Toast.makeText(ctx, "Notification Permission Required", Toast.LENGTH_SHORT).show() },
        ) {
            Text("Add Reminder")
        }
    }
}