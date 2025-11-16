package org.elnix.notes.ui.whatsnew

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

data class Update(
    val version: String,
    val changes: List<String>
)

@Composable
fun WhatsNewBottomSheet(
    updates: List<Update>,
    onDismiss: () -> Unit
) {
    val scope = rememberCoroutineScope()

    val half = 0.5f
    val full = 1f
    val sheetRatio = remember { Animatable(half) }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.5f))
            .pointerInput(Unit) {
                detectTapGestures { offset ->
//                    val sheetTop = constraints.maxHeight * (1f - sheetRatio.value)
//                    if (offset.y < sheetTop) {
                        scope.launch {
                            sheetRatio.animateTo(0f, tween(200))
                            onDismiss()
                        }
//                    }
                }
            },
        contentAlignment = Alignment.BottomCenter
    ) {
        val screenHeightPx = constraints.maxHeight.toFloat()

        val sheetHeightDp = (sheetRatio.value * screenHeightPx).dp

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(sheetHeightDp)
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(MaterialTheme.colorScheme.surface)
                .pointerInput(Unit) {
                    detectVerticalDragGestures(
                        onVerticalDrag = { change, dragAmount ->
                            change.consume()
                            val deltaRatio = dragAmount / screenHeightPx
                            val newRatio = (sheetRatio.value - deltaRatio).coerceIn(0f, full)
                            scope.launch { sheetRatio.snapTo(newRatio) }
                        },
                        onDragEnd = {
                            scope.launch {
                                when {
                                    sheetRatio.value > (half + full) / 2 -> {
                                        sheetRatio.animateTo(full, tween(250))
                                    }
                                    sheetRatio.value < half / 2 -> {
                                        sheetRatio.animateTo(0f, tween(200))
                                        onDismiss()
                                    }
                                    else -> {
                                        sheetRatio.animateTo(half, tween(250))
                                    }
                                }
                            }
                        }
                    )
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    // Only scroll when the sheet is full height
                    .verticalScroll(
                        rememberScrollState(),
                        enabled = sheetRatio.value >= full * 0.999f
                    )
            ) {
                updates.forEach { update ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Version ${update.version}",
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Spacer(Modifier.height(8.dp))
                            update.changes.forEach { change ->
                                Text("• $change", style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }
        }
    }
}
