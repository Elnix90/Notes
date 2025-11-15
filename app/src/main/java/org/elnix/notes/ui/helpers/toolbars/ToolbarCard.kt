package org.elnix.notes.ui.helpers.toolbars

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.elnix.notes.R
import org.elnix.notes.data.helpers.ClickType
import org.elnix.notes.data.helpers.GlobalActionIcon
import org.elnix.notes.data.helpers.GlobalNotesActions
import org.elnix.notes.data.helpers.TagItem
import org.elnix.notes.data.helpers.neededCLickTypeForAction
import org.elnix.notes.data.settings.stores.TagsSettingsStore
import org.elnix.notes.data.settings.stores.ToolbarItemState
import org.elnix.notes.ui.helpers.UserValidation
import org.elnix.notes.ui.helpers.tags.TagBubble
import org.elnix.notes.ui.helpers.tags.TagEditorDialog
import org.elnix.notes.ui.theme.AppObjectsColors
import org.elnix.notes.ui.theme.adjustBrightness


@Composable
fun ToolbarCard(
    ctx: Context,
    items: List<ToolbarItemState>,
    scrollState: ScrollState,
    isMultiSelect: Boolean,
    isSearchExpanded: Boolean,
    height: Dp,
    backgroundColor: Color,
    borderColor: Color,
    borderWidth: Int,
    borderRadius: Int,
    elevation: Int,
    paddingLeft: Int,
    paddingRight: Int,
    ghosted: Boolean,
    scale: Float,
    onSearchChange: ((String) -> Unit)? = null,
    onActionClick: (GlobalNotesActions, ClickType, TagItem?) -> Unit
) {
    val scope = rememberCoroutineScope()

    var showDeleteConfirm by remember { mutableStateOf(false) }
    var editTag by remember { mutableStateOf<TagItem?>(null) }
    var showEditor by remember { mutableStateOf(false) }
    var initialTag by remember { mutableStateOf<TagItem?>(null) }
    var searchText by remember { mutableStateOf("") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .graphicsLayer {
                this.scaleX = scale
                this.scaleY = scale
            }
            .padding(start = paddingLeft.dp, end = paddingRight.dp),
        shape = RoundedCornerShape(percent = borderRadius),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = if ( borderWidth > 0) BorderStroke(borderWidth.dp, borderColor) else null,
        elevation = CardDefaults.cardElevation(defaultElevation = elevation.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxSize()
                .clip(RoundedCornerShape(percent = borderRadius))
                .horizontalScroll(scrollState)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            items.filter { it.enabled }.forEach { item ->
                val action = item.action
                val neededClickTypes = neededCLickTypeForAction(action) ?: emptyList()
                when (action) {
                    GlobalNotesActions.SPACER1, GlobalNotesActions.SPACER2, GlobalNotesActions.SPACER3 -> {
                        Spacer(modifier = Modifier.weight(1f))
                    }

                    GlobalNotesActions.TAGS -> {
                        val allTags by TagsSettingsStore.getTags(ctx).collectAsState(initial = emptyList())

                        allTags.forEach { tag ->
                            TagBubble(
                                tag = tag,
                                selected = tag.selected,
                                ghostMode = ghosted,
                                onClick = { onActionClick(action, ClickType.NORMAL, tag) },
                                onLongClick = { onActionClick(action, ClickType.LONG, tag) },
                                onDelete = { onActionClick(action, ClickType.DOUBLE, tag) },
                            )
                        }
                    }
                    GlobalNotesActions.SEARCH -> {
                        if (!isSearchExpanded) {
                            searchText = ""
                            GlobalActionIcon(
                                ctx = ctx,
                                action = action,
                                color = item.color,
                                ghosted = ghosted,
                                scale = scale,
                                showButtonLabel = item.showLabel
                            ) { onActionClick(action, ClickType.NORMAL, null) }
                        } else {
                            val searchBoxColor = backgroundColor.adjustBrightness(0.8f)

                            TextField(
                                value = searchText,
                                onValueChange = { searchText = it; onSearchChange?.invoke(it) },
                                placeholder = { Text(stringResource(R.string.search)) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    capitalization = KeyboardCapitalization.Sentences,
                                    autoCorrectEnabled = true,
                                    keyboardType = KeyboardType.Unspecified
                                ),
                                leadingIcon = {
                                    if (!searchText.isEmpty()) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = stringResource(R.string.close),
                                            modifier = Modifier
                                                .clip(CircleShape)
                                                .padding(5.dp)
                                                .background(searchBoxColor)
                                                .clickable {
                                                    searchText = ""; onSearchChange?.invoke("")
                                                },
                                            tint = MaterialTheme.colorScheme.error.copy(0.7f)
                                        )
                                    } else
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = stringResource(R.string.back),
                                            modifier = Modifier
                                                .clip(CircleShape)
                                                .padding(5.dp)
                                                .background(searchBoxColor)
                                                .clickable {
                                                    onActionClick(
                                                        GlobalNotesActions.SEARCH,
                                                        ClickType.NORMAL,
                                                        null
                                                    )
                                                },
                                            tint = MaterialTheme.colorScheme.outline.copy(0.7f)
                                        )
                                },
                                colors = AppObjectsColors.outlinedTextFieldColors(
                                    backgroundColor = searchBoxColor,
                                    removeBorder = true
                                ),
                                shape = CircleShape
                            )
                        }
                    }
                    else -> {
                        if (!(isMultiSelect && action == GlobalNotesActions.EDIT_NOTE)) {
                            GlobalActionIcon(
                                ctx = ctx,
                                color = item.color,
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
                                } else null
                            )
                        }
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

