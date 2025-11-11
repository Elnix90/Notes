package org.elnix.notes.ui.security

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import org.elnix.notes.R
import org.elnix.notes.ui.theme.AppObjectsColors

@Composable
fun AskNotificationButton(activity: FragmentActivity) {
    val ctx = LocalContext.current

    IconButton(
        onClick = {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    ActivityCompat.requestPermissions(
                        activity,
                        arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                        1001
                    )
                } else {
                    // Older Android: just open app info directly
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", ctx.packageName, null)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    ctx.startActivity(intent)
                }
            } catch (e: Exception) {
                Log.e("ReminderPicker", "Permission launch failed", e)
                Toast.makeText(ctx, e.message ?: "Error asking permission", Toast.LENGTH_SHORT)
                    .show()
            }
        },
        colors = AppObjectsColors.iconButtonColors()
    ) {
        Icon(
            imageVector = Icons.Default.CalendarMonth,
            contentDescription = stringResource(R.string.allow_notif_perm)
        )
    }
}