package org.elnix.notes.ui.helpers.tags

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import org.elnix.notes.R
import org.elnix.notes.data.helpers.TagItem
import org.elnix.notes.ui.theme.AppObjectsColors

@Composable
fun TagsSection(
    allTags: List<TagItem>,
    noteTagIds: List<Long>,
    scope: CoroutineScope,
    onRemoveTagFromNote: (TagItem) -> Unit,
    onAddTagToNote: (TagItem) -> Unit
) {
    var showPicker by remember { mutableStateOf(false) }

    var showEditor by remember { mutableStateOf(false) }

    var initialTag by remember { mutableStateOf<TagItem?>(null) }

    val noteTags = remember(allTags, noteTagIds) {
        allTags.filter { noteTagIds.contains(it.id) }
    }

    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        noteTags.forEach { tag ->
            TagBubble(
                tag = tag,
                onClick = {
                    showEditor = true
                    initialTag = tag
                },
                onDelete = { onRemoveTagFromNote(tag) }
            )
        }

        // + button opens picker
        IconButton(
            onClick = { showPicker = true },
            colors = AppObjectsColors.iconButtonColors()
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(R.string.add_item),
                tint = MaterialTheme.colorScheme.outline
            )
        }
    }

    if (showPicker) {
        TagPickerDialog(
            tags = allTags,
            noteTags = noteTags,
            scope = scope,
            onDismiss = { showPicker = false },
            onPicked = {
                onAddTagToNote(it)
                showPicker = false
            }
        )
    }

    if (showEditor) {
        TagEditorDialog(
            initialTag = initialTag,
            scope = scope,
            onDismiss = { showEditor = false }
        )
    }
}
