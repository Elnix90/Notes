package org.elnix.notes.ui.theme

import androidx.compose.ui.graphics.Color


object AmoledDefault : ThemeColors {
    override val Primary = Color(0xFF6650a4)
    override val OnPrimary = Color(0xFFCECECE)
    override val Secondary = Primary.adjustBrightness(1.2f)
    override val OnSecondary = OnPrimary
    override val Tertiary = Secondary.adjustBrightness(1.2f)
    override val OnTertiary = OnPrimary
    override val Background = Color.Black
    override val OnBackground = OnPrimary
    override val Surface = Primary.blendWith(Background, 0.7f)
    override val OnSurface = OnBackground
    override val Error = Color.Red
    override val OnError = OnPrimary
    override val Outline = Color.White
    override val Delete = Color.Red
    override val Edit = Color(0xFF0019D3)
    override val Complete = Color(0xFF388E3C)
    override val Select = Color(0xFF0F7AC5)

    override val NoteTypeText = Color(0xFF05609D)
    override val NoteTypeChecklist = Color(0xFF6318A4)
    override val NoteTypeDrawing = Color(0xFFFD0990)
}

object DarkDefault : ThemeColors {
    override val Primary = Color(0xFF8F6EE8)
    override val OnPrimary = Color.Black

    override val Secondary = Primary.adjustBrightness(1.15f)
    override val OnSecondary = OnPrimary

    override val Tertiary = Secondary.adjustBrightness(1.15f)
    override val OnTertiary = OnPrimary

    override val Background = Color(0xFF1A1624)
    override val OnBackground = Color(0xFFE6E6E6)

    override val Surface = Primary.blendWith(Background, 0.75f)
    override val OnSurface = OnBackground

    override val Error = Color(0xFFCF6679)
    override val OnError = OnPrimary

    override val Outline = Color.White

    override val Delete = Color(0xFFD32F2F)
    override val Edit = Color(0xFF64B5F6)
    override val Complete = Color(0xFF81C784)
    override val Select = Color(0xFF0F7AC5)

    override val NoteTypeText = Color(0xFF05609D)
    override val NoteTypeChecklist = Color(0xFF6318A4)
    override val NoteTypeDrawing = Color(0xFFFD0990)
}


object LightDefault : ThemeColors {
    override val Primary = Color(0xFFA351E7)
    override val OnPrimary = Color.Black
    override val Secondary = Primary.adjustBrightness(0.8f)
    override val OnSecondary = OnPrimary
    override val Tertiary = Secondary.adjustBrightness(0.8f)
    override val OnTertiary = OnPrimary
    override val Background = Color.White
    override val OnBackground = OnPrimary
    override val Surface = Primary.blendWith(Background, 0.7f)
    override val OnSurface = OnPrimary
    override val Error = Color.Red
    override val OnError = OnPrimary
    override val Outline = Color.Black
    override val Delete = Color.Red
    override val Edit = Color(0xFF0020FF)
    override val Complete = Color(0xFF388E3C)
    override val Select = Color(0xFF0F7AC5)

    override val NoteTypeText = Color(0xFF05609D)
    override val NoteTypeChecklist = Color(0xFF6318A4)
    override val NoteTypeDrawing = Color(0xFFFD0990)
}

