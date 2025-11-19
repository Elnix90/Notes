package org.elnix.notes.ui.helpers.tags

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.elnix.notes.R
import org.elnix.notes.data.helpers.TagItem
import org.elnix.notes.data.settings.stores.TagsSettingsStore
import org.elnix.notes.ui.helpers.UserValidation
import org.elnix.notes.ui.helpers.colors.ColorPickerRow
import org.elnix.notes.ui.theme.AppObjectsColors

@Composable
fun TagEditorDialog(
    initialTag: TagItem? = null,
    scope: CoroutineScope,
    onDismiss: () -> Unit
) {
    val ctx = LocalContext.current
    var name by remember { mutableStateOf(initialTag?.name ?: "") }
    var colorInt by remember {
        mutableIntStateOf(initialTag?.color?.toArgb() ?: 0xFF2196F3.toInt())
    }
    var showConfirmDelete by remember { mutableStateOf(false) }
    var emptyFieldNotStart by remember { mutableStateOf(false) }

    AlertDialog(
        containerColor = MaterialTheme.colorScheme.surface,
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (initialTag == null) stringResource(R.string.create_new_tag) else stringResource(R.string.edit_tag),
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                OutlinedTextField(
                    isError = emptyFieldNotStart,
                    value = name,
                    onValueChange = { name = it; emptyFieldNotStart = it.isEmpty() },
                    label = {
                        Text(
                            text = stringResource(R.string.tag_name),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    singleLine = true,
                    colors = AppObjectsColors.outlinedTextFieldColors(
                        backgroundColor = MaterialTheme.colorScheme.surface
                    )
                )

                ColorPickerRow(
                    label = stringResource(R.string.tag),
                    defaultColor = Color(colorInt),
                    currentColor = colorInt,
                    backgroundColor = MaterialTheme.colorScheme.primary,
                    onColorPicked = { colorInt = it }
                )

                if (initialTag != null) {
                    OutlinedButton(
                        onClick = { showConfirmDelete = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = AppObjectsColors.cancelButtonColors()
                    ) {
                        Text(
                            text = stringResource(R.string.delete_tag),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (name.isNotBlank()) {
                    scope.launch {
                        val newTag = initialTag?.copy(name = name, color = Color(colorInt))
                            ?: TagItem(id = System.currentTimeMillis(), name = name, color = Color(colorInt))
                        if (initialTag == null) TagsSettingsStore.addTag(ctx, newTag)
                        else TagsSettingsStore.updateTag(ctx, newTag)
                        onDismiss()
                    }
                } else {
                    emptyFieldNotStart = true
                }
            }) {
                Text(
                    text = stringResource(R.string.save),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.cancel),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    )

    if (showConfirmDelete && initialTag != null) {
        UserValidation(
            title = stringResource(R.string.delete_tag),
            message = "${stringResource(R.string.tag_deletion_confirm)} '${initialTag.name}'?",
            onCancel = { showConfirmDelete = false },
            onAgree = {
                showConfirmDelete = false
                scope.launch {
                    TagsSettingsStore.deleteTag(ctx, initialTag)
                    onDismiss()
                }
            }
        )
    }
}

