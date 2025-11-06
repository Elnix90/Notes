package org.elnix.notes.ui.helpers.toolbars

import android.content.Context
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.elnix.notes.R
import org.elnix.notes.data.helpers.ClickType
import org.elnix.notes.data.helpers.GlobalActionIcon
import org.elnix.notes.data.helpers.GlobalNotesActions
import org.elnix.notes.data.helpers.TagItem
import org.elnix.notes.data.settings.stores.TagsSettingsStore
import org.elnix.notes.data.settings.stores.ToolbarItemState
import org.elnix.notes.ui.helpers.UserValidation
import org.elnix.notes.ui.helpers.tags.TagBubble
import org.elnix.notes.ui.helpers.tags.TagEditorDialog


@Composable
fun ToolbarCard(
    ctx: Context,
    items: List<ToolbarItemState>,
    scrollState: ScrollState,
    color: Color,
    ghosted: Boolean,
    scale: Float,
    onActionClick: (GlobalNotesActions, ClickType, TagItem?) -> Unit
) {
    val scope = rememberCoroutineScope()
    val allTags by TagsSettingsStore.getTags(ctx).collectAsState(initial = emptyList())

    var showDeleteConfirm by remember { mutableStateOf(false) }
    var editTag by remember { mutableStateOf<TagItem?>(null) }
    var showEditor by remember { mutableStateOf(false) }
    var initialTag by remember { mutableStateOf<TagItem?>(null) }


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                this.scaleX = scale
                this.scaleY = scale
            }
            .alpha(if (ghosted) 0.6f else 1f)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        shape = CircleShape,
        colors = CardDefaults.cardColors(containerColor = color),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .clip(CircleShape)
                .horizontalScroll(scrollState)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            items.filter { it.enabled }.forEach { item ->
                val action = item.action
                when (action) {
                    GlobalNotesActions.SPACER1, GlobalNotesActions.SPACER2, GlobalNotesActions.SPACER3 -> {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                    GlobalNotesActions.TAGS -> {
                        // --- Tag bubbles ---
                        allTags.forEach { tag ->
                            TagBubble(
                                tag = tag,
                                selected = tag.selected,
                                onClick = { onActionClick(action, ClickType.NORMAL, tag) },
                                onLongClick =  { onActionClick(action, ClickType.LONG, tag) },
                                onDelete =  { onActionClick(action, ClickType.DOUBLE, tag) },
                            )
                        }
                    }
                    else -> {
                        GlobalActionIcon(
                            ctx = ctx,
                            action = action,
                            ghosted = ghosted,
                            scale = scale,
                            showButtonLabel = item.showLabel,
                            onClick = { onActionClick(action, ClickType.NORMAL, null) },
                            onLongClick = if (ClickType.LONG in neededClickTypes) {
                                { onActionClick(action, ClickType.LONG, null) }
                            } else null,
                            onDoubleClick = if (ClickType.DOUBLE in neededClickTypes) {
                                { onActionClick(action, ClickType.DOUBLE, null) }
                            } else null,
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

