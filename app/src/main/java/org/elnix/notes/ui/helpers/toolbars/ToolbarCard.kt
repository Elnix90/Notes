package org.elnix.notes.ui.helpers.toolbars

import android.content.Context
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import org.elnix.notes.data.helpers.GlobalActionIcon
import org.elnix.notes.data.helpers.GlobalNotesActions
import org.elnix.notes.data.settings.stores.UiSettingsStore


@Composable
fun ToolbarCard(
    ctx: Context,
    actions: List<GlobalNotesActions>,
    scrollState: ScrollState,
    color: Color,
    ghosted: Boolean,
    scale: Float,
    onActionClick: (GlobalNotesActions) -> Unit
) {
    val searchExpanded by UiSettingsStore.getShowSearchText(ctx).collectAsState(initial = true)


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                this.scaleX = scale
                this.scaleY = scale
            }
            .alpha(if (ghosted) 0.6f else 1f)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .horizontalScroll(scrollState)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            actions.forEach { action ->
                if (action == GlobalNotesActions.SPACER) {
                    Spacer(modifier = Modifier.weight(1f))
                } else {
                    GlobalActionIcon(
                        ctx = ctx,
                        action = action,
                        ghosted = ghosted,
                        scale = scale,
                        searchExpanded = searchExpanded,
                        onClick = onActionClick
                    )
                }
            }
        }
    }
}

