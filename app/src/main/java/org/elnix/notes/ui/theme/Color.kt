package org.elnix.notes.ui.theme

import androidx.compose.ui.graphics.Color

val PrimaryDefault = Color(0xFF6650a4)
val OnPrimaryDefault = Color(0xFFCECECE)
val Secondary40 = PrimaryDefault.adjustBrightness(0.2f)
val OnSecondaryDefault = OnPrimaryDefault
val Tertiary40 = Secondary40.adjustBrightness(0.2f)
val OnTertiaryDefault = OnPrimaryDefault
val BackgroundDefault = Color.Black
val OnBackgroundDefault = OnPrimaryDefault
val SurfaceDefault = PrimaryDefault.blendWith(BackgroundDefault, 0.7f)
val OnSurfaceDefault = OnPrimaryDefault
val ErrorDefault = Color.Red
val OnErrorDefault = Color.White
val OutlineDefault = Color.White
val DeleteDefault = Color.Red
val EditDefault = Color(0xFF0083FF)
val CompleteDefault = Color(0xFF388E3C)
