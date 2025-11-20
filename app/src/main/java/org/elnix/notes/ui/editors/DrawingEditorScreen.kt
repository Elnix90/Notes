package org.elnix.notes.ui.editors

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import org.elnix.notes.ui.NoteViewModel
import org.elnix.notes.ui.helpers.colors.ColorPickerRow

data class Stroke(val color: Color, val width: Float, val points: List<Offset>)

@Composable
fun DrawingEditorScreen(
    vm: NoteViewModel,
    noteId: Long?,
    onSaved: () -> Unit,
    onCancel: () -> Unit
) {
    var strokes by remember { mutableStateOf(listOf<Stroke>()) }
    var currentStroke by remember { mutableStateOf<Stroke?>(null) }
    var brushColor by remember { mutableStateOf(Color.Black) }
    var brushSize by remember { mutableFloatStateOf(6f) }
    val favorites = remember { mutableStateListOf<Color>() }

    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    Scaffold(
        bottomBar = {
            Column(Modifier.background(MaterialTheme.colorScheme.surface)) {
                ColorPickerRow(
                    label = "Brush",
                    showLabel = false,
                    defaultColor = Color.Black,
                    currentColor = brushColor,
                    backgroundColor = MaterialTheme.colorScheme.background
                ) { pickedColor ->
                    val picked = pickedColor
                    brushColor = picked
                    if (!favorites.contains(picked)) favorites.add(picked)
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    favorites.forEach { color ->
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(color, shape = MaterialTheme.shapes.small)
                                .clickable { brushColor = color }
                                .combinedClickable(
                                    onLongClick = { favorites.remove(color) },
                                    onClick = { brushColor = color }
                                )
                        )
                    }
                }

                Slider(
                    value = brushSize,
                    onValueChange = { brushSize = it },
                    valueRange = 2f..30f,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { strokes = emptyList() }) {
                Icon(Icons.Default.Delete, contentDescription = "Clear")
            }
        }
    ) { padding ->
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White)
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        scale *= zoom
                        offset += pan
                    }
                }
                .pointerInput(Unit) {
                    detectTransformGestures { centroid, pan, zoom, _ ->
                        if (zoom == 1f && pan != Offset.Zero) {
                            val point = (centroid - offset) / scale
                            currentStroke = currentStroke?.copy(points = currentStroke!!.points + point)
                        } else if (zoom != 1f) {
                            scale *= zoom
                        }
                    }
                }
        ) {
            withTransform({
                scale(scale)
                translate(offset.x, offset.y)
            }) {
                strokes.forEach { stroke ->
                    val path = Path().apply {
                        stroke.points.firstOrNull()?.let { moveTo(it.x, it.y) }
                        stroke.points.drop(1).forEach { lineTo(it.x, it.y) }
                    }
                    drawPath(path, stroke.color, style = androidx.compose.ui.graphics.drawscope.Stroke(stroke.width))
                }

                currentStroke?.let { stroke ->
                    val path = Path().apply {
                        stroke.points.firstOrNull()?.let { moveTo(it.x, it.y) }
                        stroke.points.drop(1).forEach { lineTo(it.x, it.y) }
                    }
                    drawPath(path, stroke.color, style = androidx.compose.ui.graphics.drawscope.Stroke(stroke.width))
                }
            }
        }
    }
}
