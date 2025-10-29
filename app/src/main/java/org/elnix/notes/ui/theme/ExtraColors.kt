package org.elnix.notes.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class ExtraColors(
    val delete: Color,
    val edit: Color,
    val complete: Color
)

// default fallback values
val LocalExtraColors = staticCompositionLocalOf {
    ExtraColors(
        delete = AmoledDefault.Delete,
        edit = AmoledDefault.Edit,
        complete = AmoledDefault.Complete
    )
}
