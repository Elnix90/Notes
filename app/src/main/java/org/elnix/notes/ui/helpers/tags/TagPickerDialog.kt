package org.elnix.notes.ui.helpers.tags

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.elnix.notes.R
import org.elnix.notes.data.helpers.TagItem
import org.elnix.notes.data.settings.stores.TagsSettingsStore
import org.elnix.notes.data.settings.stores.UserConfirmEntry
import org.elnix.notes.data.settings.stores.UserConfirmSettingsStore
import org.elnix.notes.ui.helpers.UserValidation

@Composable
fun TagPickerDialog(
    tags: List<TagItem>,
    noteTags: List<TagItem>,
    scope: CoroutineScope,
    onDismiss: () -> Unit,
    onPicked: (TagItem) -> Unit
) {
    val ctx = LocalContext.current

    val showDeleteTagConfirmation by UserConfirmSettingsStore.get(
        ctx = ctx,
        entry = UserConfirmEntry.SHOW_USER_VALIDATION_DELETE_TAG
    ).collectAsState(initial = true)

    var showDeleteConfirm by remember { mutableStateOf(false) }
    var showEditor by remember { mutableStateOf(false) }
    var editTag by remember { mutableStateOf<TagItem?>(null) }

    AlertDialog(
        containerColor = MaterialTheme.colorScheme.surface,
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.pick_a_tag),
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(5.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        tags.forEach { tag ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onPicked(tag) }
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(tag.color.copy(0.2f))
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TagBubble(tag, onClick = { onPicked(tag) })

                                // Edit + Delete buttons
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    IconButton(
                                        onClick = { editTag = tag; showEditor = true }
                                    ) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit Tag")
                                    }
                                    val deleteButtonEnabled = !noteTags.any { it.id == tag.id }
                                    IconButton(
                                        onClick = {
                                            if (deleteButtonEnabled) {
                                                if (showDeleteTagConfirmation) {
                                                    editTag = tag
                                                    showDeleteConfirm = true
                                                } else scope.launch {
                                                    TagsSettingsStore.deleteTag(
                                                        ctx,
                                                        tag
                                                    )
                                                }
                                            } else {
                                                Toast.makeText(ctx, ctx.getString(R.string.this_tag_is_used_by_other_notes),
                                                    Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    ) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Delete Tag",
                                            tint = MaterialTheme.colorScheme.outline.copy(if (deleteButtonEnabled) 1f else 0.5f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Button(
                        onClick = { editTag = null; showEditor = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(R.string.add_item),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                        Text(
                            text = stringResource(R.string.create_new_tag),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.close),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    )

    if (showEditor) {
        TagEditorDialog(
            initialTag = editTag,
            scope = scope,
            onDismiss = { showEditor = false }
        )
    }

    if (showDeleteConfirm && editTag != null) {
        val tagToDelete = editTag!!
        UserValidation(
            title = stringResource(R.string.delete_tag),
            message = "${stringResource(R.string.tag_deletion_confirm)} '${tagToDelete.name}'?",
            doNotRemindMeAgain = {
                scope.launch {
                    UserConfirmSettingsStore.set(ctx, UserConfirmEntry.SHOW_USER_VALIDATION_DELETE_TAG, false)
                }
            },
            onCancel = { showDeleteConfirm = false },
            onAgree = {
                showDeleteConfirm = false
                scope.launch {
                    TagsSettingsStore.deleteTag(ctx, tagToDelete)
                }
            }
        )
    }
}