package org.elnix.notes.utils

import android.Manifest
import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import org.elnix.notes.ui.helpers.StyledReminderDialogs
import java.util.Calendar

@Composable
fun ReminderPicker(onPicked: (ReminderOffset) -> Unit) {
    val ctx = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var hasPermission by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        hasPermission = ctx.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) ==
                android.content.pm.PackageManager.PERMISSION_GRANTED
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                hasPermission = ctx.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) ==
                        android.content.pm.PackageManager.PERMISSION_GRANTED
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    var tempCalendar by remember { mutableStateOf(Calendar.getInstance()) }

    if (!hasPermission) {
        Button(
            onClick = {
                try {
                    ActivityCompat.requestPermissions(
                        ctx as Activity,
                        arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                        1001
                    )
                } catch (e: Exception) {
                    Log.e("ReminderPicker", "Permission launch failed", e)
                    Toast.makeText(ctx, e.message ?: "Error", Toast.LENGTH_SHORT).show()
                }
            }
        ) {
            Text(
                text = "Allow Notifications permission",
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    } else {
        StyledReminderDialogs(tempCalendar = tempCalendar, onPicked = onPicked)
    }
}

