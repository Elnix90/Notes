package org.elnix.notes.ui.helpers.toolbars

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.elnix.notes.R
import org.elnix.notes.data.helpers.TagItem
import org.elnix.notes.data.settings.stores.TagsSettingsStore
import org.elnix.notes.ui.helpers.UserValidation
import org.elnix.notes.ui.helpers.tags.TagBubble
import org.elnix.notes.ui.helpers.tags.TagEditorDialog
import org.elnix.notes.ui.theme.LocalExtraColors

@Composable
fun TagSelectingRow(
    ctx: Context,
    allTags: List<TagItem>,
    scope: CoroutineScope,
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var editTag by remember { mutableStateOf<TagItem?>(null) }
    var showEditor by remember { mutableStateOf(false) }
    var initialTag by remember { mutableStateOf<TagItem?>(null) }


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 10.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // --- Select All Button ---
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(LocalExtraColors.current.select)
                    .combinedClickable(
                        onClick = {
                            scope.launch {
                                TagsSettingsStore.setAllTagsSelected(ctx, true)
                            }
                        },
                        onLongClick = {
                            scope.launch {
                                TagsSettingsStore.setAllTagsSelected(ctx, false)
                            }
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SelectAll,
                    contentDescription = stringResource(R.string.reset_filter),
                    tint = MaterialTheme.colorScheme.outline
                )
            }

            // --- Tag bubbles ---
            allTags.forEach { tag ->
                TagBubble(
                    tag = tag,
                    selected = tag.selected,
                    onClick = {
                        scope.launch {
                            TagsSettingsStore.updateTag(ctx, tag.copy(selected = !tag.selected))
                        }
                    },
                    onLongClick = {
                        showEditor = true
                        initialTag = tag
                    },
                    onDelete = {
                        editTag = tag
                        showDeleteConfirm = true
                    }
                )
            }

            // --- Add Tag Button ---
            Box(
                modifier = Modifier
                    .wrapContentSize()
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(8.dp)
                    .clickable { showEditor = true },
                contentAlignment = Alignment.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.add_tag),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )


                    if (allTags.isEmpty()) {
                        Text(
                            text = stringResource(R.string.add_tag),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }


        }
    }

    // --- Dialogs ---
    if (showEditor) {
        TagEditorDialog(
            initialTag = initialTag,
            scope = scope,
            onDismiss = { showEditor = false }
        )
    }

    if (showDeleteConfirm && editTag != null) {
        val tagToDelete = editTag!!
        UserValidation(
            title = stringResource(R.string.delete_tag),
            message = "${stringResource(R.string.tag_deletion_confirm)} '${tagToDelete.name}'?",
            onCancel = { showDeleteConfirm = false },
            onAgree = {
                showDeleteConfirm = false
                scope.launch { TagsSettingsStore.deleteTag(ctx, tagToDelete) }
            }
        )
    }
}
