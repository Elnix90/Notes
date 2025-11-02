package org.elnix.notes.ui.helpers.colors

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import org.elnix.notes.data.NoteEntity
import org.elnix.notes.ui.NoteViewModel
import kotlin.random.Random

suspend fun updateNoteBgColor(
    currentId: Long?,
    vm: NoteViewModel,
    pickedColor: Color
): NoteEntity? {
    currentId ?: return null
    val n = vm.getById(currentId) ?: return null

    val autoText = n.autoTextColor
    val computedTextColor = if (autoText) {
        if (pickedColor.luminance() < 0.5f) Color.White else Color.Black
    } else n.txtColor

    val updated = n.copy(
        bgColor = pickedColor,
        txtColor = computedTextColor
    )
    vm.update(updated)
    return updated
}

suspend fun updateNoteTextColor(
    currentId: Long?,
    vm: NoteViewModel,
    pickedColor: Color
): NoteEntity? {
    currentId ?: return null
    val n = vm.getById(currentId) ?: return null
    val updated = n.copy(txtColor = pickedColor)
    vm.update(updated)
    return updated
}

suspend fun toggleAutoColor(
    currentId: Long?,
    vm: NoteViewModel,
    checked: Boolean
): NoteEntity? {
    currentId ?: return null
    val n = vm.getById(currentId) ?: return null
    val computedTxt = if (checked) {
        val bg = n.bgColor
        if (bg.luminance() < 0.4f) Color.White else Color.Black
    } else n.txtColor

    val updated = n.copy(
        autoTextColor = checked,
        txtColor = computedTxt
    )
    vm.update(updated)
    return updated
}

suspend fun setRandomColor(
    currentId: Long?,
    vm: NoteViewModel,
    autoTextColor: Boolean
): NoteEntity? {
    currentId ?: return null
    val n = vm.getById(currentId) ?: return null
    val color = Color(
        red = Random.nextFloat(),
        green = Random.nextFloat(),
        blue = Random.nextFloat()
    )

    val computedTxt = if (autoTextColor) {
        val bg = n.bgColor
        if (bg.luminance() < 0.4f) Color.White else Color.Black
    } else n.txtColor

    val updated = n.copy(
        bgColor = color,
        txtColor = computedTxt
    )

    vm.update(updated)
    return updated
}